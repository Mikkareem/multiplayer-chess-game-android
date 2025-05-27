package com.techullurgy.chess.data

import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.domain.GameNotAvailableState
import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.JoinedGameLoadingState
import com.techullurgy.chess.domain.JoinedGameState
import com.techullurgy.chess.domain.JoinedGameStateHeader
import com.techullurgy.chess.domain.JoinedGameStateHeaderList
import com.techullurgy.chess.domain.NetworkNotAvailableState
import com.techullurgy.chess.domain.SomethingWentWrongState
import com.techullurgy.chess.domain.UserDisconnectedState
import com.techullurgy.chess.domain.events.CellSelectionEvent
import com.techullurgy.chess.domain.events.ClientGameEvent
import com.techullurgy.chess.domain.events.DisconnectedEvent
import com.techullurgy.chess.domain.events.EnterRoomEvent
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.GameNotAvailableEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
import com.techullurgy.chess.domain.events.NetworkNotAvailableEvent
import com.techullurgy.chess.domain.events.NotYetAnyEventAvailableEvent
import com.techullurgy.chess.domain.events.PieceMoveEvent
import com.techullurgy.chess.domain.events.ResetSelectionDoneEvent
import com.techullurgy.chess.domain.events.ResetSelectionEvent
import com.techullurgy.chess.domain.events.SelectionResultEvent
import com.techullurgy.chess.domain.events.ServerGameEvent
import com.techullurgy.chess.domain.events.SomethingWentWrongEvent
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import com.techullurgy.chess.domain.events.UserDisconnectedEvent
import com.techullurgy.chess.domain.repository.GameRepository
import com.techullurgy.chess.presentation.models.GameNotAvailableScreenState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.io.IOException
import kotlin.coroutines.coroutineContext

internal class GameRepositoryImpl(
    private val broker: GameRoomMessageBroker,
    private val gameDao: GameDao,
    applicationScope: CoroutineScope
): GameRepository {
    private val updateEvents = broker.observeAndUpdateGameEventDatabase()
        .withSideEffectFlow(broker.observeNecessityOfWebsocketConnection())
        .onStart {
            gameDao.invalidateJoinedRooms()
            broker.fetchAnyJoinedRoomsAvailable()
        }
        .catch {
            if(it is IOException) {
                emit(NetworkNotAvailableEvent)
            } else {
                SomethingWentWrongEvent
            }
        }
        .onCompletion { cause ->
            println("onCompletion Called")
            if(cause is CancellationException) {
                try {
                    gameDao.invalidateJoinedRooms()
                } catch (e: CancellationException) {
                    CoroutineScope(Dispatchers.IO).async {
                        launch {
                            delay(5000)
                            this@async.cancel()
                        }
                        gameDao.invalidateJoinedRooms()
                        cancel()
                    }.await()
                    coroutineContext.ensureActive()
                }
            }
        }
        .transform {
            if(it !is ServerGameEvent) {
                emit(it)
            } else {
                if(it is GameLoadingEvent) {
                    emit(it)
                } else {
                    emit(null)
                }
            }
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NotYetAnyEventAvailableEvent
        )

    override fun getJoinedGame(roomId: String): Flow<GameState> = combine(
        updateEvents,
        gameDao.observeJoinedGame(roomId)
            .distinctUntilChanged()
    ) { a, b ->
        if(a != null) {
            return@combine when(a) {
                is GameLoadingEvent -> {
                    assert(a.roomId != roomId)
                    JoinedGameLoadingState
                }
                NetworkNotAvailableEvent -> NetworkNotAvailableState
                UserDisconnectedEvent -> UserDisconnectedState
                GameNotAvailableEvent -> GameNotAvailableState
                SomethingWentWrongEvent -> SomethingWentWrongState
                NotYetAnyEventAvailableEvent -> JoinedGameLoadingState
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
    }.distinctUntilChanged()

    override fun getJoinedGamesList(): Flow<GameState> = combine(
        updateEvents,
        gameDao.observeJoinedGamesList().distinctUntilChanged()
    ) { a, b ->

        if(a != null) {
            return@combine when(a) {
                // Solve for Loading List
                is GameLoadingEvent,
                NotYetAnyEventAvailableEvent -> JoinedGameLoadingState
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
}

private fun <T1, T2> Flow<T1>.withSideEffectFlow(sideEffect: Flow<T2>): Flow<T1> = callbackFlow {
    sideEffect.launchIn(this)
    collect {
        send(it)
    }
    awaitClose()
}