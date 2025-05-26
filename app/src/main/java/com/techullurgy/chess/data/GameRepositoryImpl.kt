package com.techullurgy.chess.data

import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.JoinedGameLoadingState
import com.techullurgy.chess.domain.JoinedGameState
import com.techullurgy.chess.domain.JoinedGameStateHeader
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

internal class GameRepositoryImpl(
    private val broker: GameRoomMessageBroker,
    private val gameDao: GameDao,
    applicationScope: CoroutineScope
): GameRepository {
    private val updateEvents = broker.observeAndUpdateGameEventDatabase()
        .withSideEffectFlow(broker.observeNecessityOfWebsocketConnection())
        .transform {
            // TODO: If event is network based event, emit Network Event else emit null
            emit(it)
        }
        .stateIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    override fun getJoinedGame(roomId: String): Flow<GameState> = combine(
        updateEvents,
        gameDao.observeJoinedGame(roomId)
            .distinctUntilChanged()
    ) { a, b ->
        if(a == null || b == null) {
            JoinedGameLoadingState
        } else {
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
    }

    override fun getJoinedGamesList(): Flow<List<JoinedGameStateHeader>> = combine(
        updateEvents,
        gameDao.observeJoinedGamesList().distinctUntilChanged()
    ) { a, b ->
        a?.let {
            b.map { entity ->
                JoinedGameStateHeader(
                    roomId = entity.roomId,
                    roomName = entity.roomName,
                    membersCount = entity.membersCount,
                    isMyTurn = entity.isMyTurn,
                    yourTime = entity.yourTime,
                    opponentTime = entity.opponentTime
                )
            }
        } ?: emptyList()
    }
}

private fun <T1, T2> Flow<T1>.withSideEffectFlow(sideEffect: Flow<T2>): Flow<T1> = callbackFlow {
    sideEffect.launchIn(this)
    collect {
        send(it)
    }
}