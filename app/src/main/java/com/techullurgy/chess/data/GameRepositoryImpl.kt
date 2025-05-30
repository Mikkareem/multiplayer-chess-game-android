package com.techullurgy.chess.data

import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.data.dto.toGameRoom
import com.techullurgy.chess.data.dto.toGameRoomDto
import com.techullurgy.chess.domain.GameNotAvailableState
import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.JoinedGameState
import com.techullurgy.chess.domain.JoinedGameStateHeader
import com.techullurgy.chess.domain.JoinedGameStateHeaderList
import com.techullurgy.chess.domain.NetworkLoadingState
import com.techullurgy.chess.domain.NetworkNotAvailableState
import com.techullurgy.chess.domain.SomethingWentWrongState
import com.techullurgy.chess.domain.UserDisconnectedState
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.GameNotAvailableEvent
import com.techullurgy.chess.domain.events.NetworkLoadingEvent
import com.techullurgy.chess.domain.events.NetworkNotAvailableEvent
import com.techullurgy.chess.domain.events.RetryFetchingEvent
import com.techullurgy.chess.domain.events.ServerGameEvent
import com.techullurgy.chess.domain.events.SomethingWentWrongEvent
import com.techullurgy.chess.domain.events.UserConnectedEvent
import com.techullurgy.chess.domain.events.UserDisconnectedEvent
import com.techullurgy.chess.domain.models.GameRoom
import com.techullurgy.chess.domain.repository.GameRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.io.IOException
import kotlin.coroutines.coroutineContext

internal class GameRepositoryImpl(
    private val broker: GameRoomMessageBroker,
    private val gameApi: ChessGameApi,
    private val gameDao: GameDao,
    applicationScope: CoroutineScope
): GameRepository {

    private val retryEventChannel = Channel<RetryFetchingEvent>()

    private val updateEvents = merge(
            broker.observeAndUpdateGameEventDatabase(),
            retryEventChannel.consumeAsFlow()
        ).transform { event ->
            when(event) {
                RetryFetchingEvent -> {
                    emit(NetworkLoadingEvent)
                    broker.fetchAnyJoinedRoomsAvailable()
                    emit(null)
                }
                else -> emit(event)
            }
        }
        .withSideEffectFlow(broker.observeNecessityOfWebsocketConnection())
        .onCompletion { cause ->
            try {
                gameDao.invalidateJoinedRooms()
            } catch (_: CancellationException) {
                withContext(NonCancellable) {
                    withTimeout(5000) {
                        gameDao.invalidateJoinedRooms()
                    }
                }
                coroutineContext.ensureActive()
            }
        }
        .transform {
            if(it !is ServerGameEvent) {
                if(it == UserConnectedEvent) {
                    emit(null)
                } else {
                    emit(it)
                }
            } else {
                emit(null)
            }
        }
        .onStart {
            emit(NetworkLoadingEvent)
            gameDao.invalidateJoinedRooms()
            broker.fetchAnyJoinedRoomsAvailable()
            emit(null)
        }
        .catch {
            if(it is IOException) {
                emit(NetworkNotAvailableEvent)
            } else if(it is NoGamesFoundException) {
                emit(GameNotAvailableEvent)
            } else {
                emit(SomethingWentWrongEvent)
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkLoadingEvent
        )

    @OptIn(FlowPreview::class)
    override fun getJoinedGame(roomId: String): Flow<GameState> = combine(
        updateEvents,
        gameDao.observeJoinedGame(roomId)
    ) { a, b ->
        if(a != null) {
            return@combine when(a) {
                NetworkLoadingEvent -> NetworkLoadingState
                NetworkNotAvailableEvent -> NetworkNotAvailableState
                UserDisconnectedEvent -> UserDisconnectedState
                GameNotAvailableEvent -> GameNotAvailableState
                SomethingWentWrongEvent -> SomethingWentWrongState
                else -> TODO()
            }
        }

        if(b == null) {
            return@combine GameNotAvailableState
        }

        JoinedGameState(
            roomId = b.roomId,
            roomName = b.roomName,
            members = b.members.split("***"),
            board = listOf(), // TODO(),
            assignedColor = b.assignedColor,
            lastMove = 1 to 1, // TODO()
            isMyTurn = b.isMyTurn,
            availableMoves = listOf(), // TODO(),
            cutPieces = listOf(), // TODO(),
            yourTime = b.yourTime,
            opponentTime = b.opponentTime
        )
    }
        .debounce(100)
        .distinctUntilChanged()

    override fun getJoinedGamesList(): Flow<GameState> = combine(
        updateEvents,
        gameDao.observeJoinedGamesList()
    ) { a, b ->

        if(a != null) {
            return@combine when(a) {
                NetworkLoadingEvent -> NetworkLoadingState
                NetworkNotAvailableEvent -> NetworkNotAvailableState
                UserDisconnectedEvent -> UserDisconnectedState
                GameNotAvailableEvent -> GameNotAvailableState
                SomethingWentWrongEvent -> SomethingWentWrongState
                else -> TODO()
            }
        }

        val result = b.map { entity ->
            JoinedGameStateHeader(
                roomId = entity.roomId,
                roomName = entity.roomName,
                membersCount = entity.membersCount,
                isMyTurn = entity.isMyTurn,
                yourTime = entity.yourTime,
                opponentTime = entity.opponentTime
            )
        }

        JoinedGameStateHeaderList(value = result)
    }.distinctUntilChanged()

    override suspend fun createRoom(room: GameRoom) {
        gameApi.createRoom(room.toGameRoomDto())
    }

    override suspend fun joinRoom(roomId: String) {
        gameApi.joinRoom(roomId)
    }

    override suspend fun getCreatedRoomsByMe(): List<GameRoom> {
        return gameApi.getCreatedRoomsByMe().map { it.toGameRoom() }
    }

    override suspend fun retry() {
        retryEventChannel.send(RetryFetchingEvent)
    }
}

private fun <T1, T2> Flow<T1>.withSideEffectFlow(sideEffect: Flow<T2>): Flow<T1> = callbackFlow {
    sideEffect.launchIn(this)
    collect {
        send(it)
    }
    awaitClose()
}