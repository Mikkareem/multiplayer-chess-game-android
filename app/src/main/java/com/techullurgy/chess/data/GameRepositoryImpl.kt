package com.techullurgy.chess.data

import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.GameStateHeader
import com.techullurgy.chess.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class GameRepositoryImpl(
    private val broker: GameRoomMessageBroker,
    private val gameDao: GameDao,
    applicationScope: CoroutineScope
): GameRepository {
    override val updateEvents = broker.observeAndUpdateGameEventDatabase()
        .shareIn(
            scope = applicationScope,
            started = SharingStarted.WhileSubscribed(5000)
        )

    override fun observeSessions(): Flow<Boolean> = broker.observeNecessityOfWebsocketConnection()

    override fun getJoinedGame(roomId: String): Flow<GameState> = gameDao.observeJoinedGame(roomId)
        .distinctUntilChanged()
        .map {
            GameState(
                roomId = it.roomId,
                roomName = it.roomName,
                members = it.members.split("***"),
                board = listOf(), // TODO(),
                assignedColor = it.assignedColor,
                lastMove = 1 to 1, // TODO()
                isMyTurn = it.isMyTurn,
                availableMoves = listOf(), // TODO(),
                cutPieces = listOf(), // TODO(),
                yourTime = it.yourTime,
                opponentTime = it.opponentTime
            )
        }

    override fun getJoinedGamesList(): Flow<List<GameStateHeader>> = gameDao.observeJoinedGamesList()
        .distinctUntilChanged()
        .map {
            it.map { entity ->
                GameStateHeader(
                    roomId = entity.roomId,
                    roomName = entity.roomName,
                    membersCount = entity.membersCount,
                    isMyTurn = entity.isMyTurn,
                    yourTime = entity.yourTime,
                    opponentTime = entity.opponentTime
                )
            }
    }
}