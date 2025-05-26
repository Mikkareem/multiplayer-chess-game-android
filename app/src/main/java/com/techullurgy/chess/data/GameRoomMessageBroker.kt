package com.techullurgy.chess.data

import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.data.db.TimerEntity
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
import com.techullurgy.chess.domain.events.ResetSelectionDoneEvent
import com.techullurgy.chess.domain.events.SelectionResultEvent
import com.techullurgy.chess.domain.events.ServerGameEvent
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform

internal class GameRoomMessageBroker(
    private val gameDao: GameDao,
    private val gameApi: ChessGameApi,
) {
    fun observeNecessityOfWebsocketConnection() =
        observeDesiredGameEntities()
            .transform {
                if(!it.isEmpty()) {
                    if(!gameApi.isSocketActive) {
                        emit(true)
                    }
                } else {
                    emit(false)
                }
            }
            .distinctUntilChanged()
            .onEach {
                if(it) {
                    gameApi.startSession()
                } else {
                    gameApi.stopSession()
                }
            }

    fun observeAndUpdateGameEventDatabase() =
        gameApi.gameEventsFlow
            .transform {
                if(it is ServerGameEvent) {
                    when(it) {
                        is GameLoadingEvent -> emit(GameLoadingEvent(it.roomId))
                        is GameUpdateEvent -> {
                            val assignedColor = gameDao.getAssignedColor(it.roomId)
                            gameDao.updateGame(
                                roomId = it.roomId,
                                board = it.board,
                                lastMove = it.lastMove,
                                isMyTurn = it.currentTurn == assignedColor,
                                cutPieces = it.cutPieces,
                                kingInCheckIndex = it.kingInCheckIndex,
                                gameOver = it.gameOver
                            )
                        }
                        is ResetSelectionDoneEvent -> gameDao.resetSelection(roomId = it.roomId)
                        is SelectionResultEvent -> gameDao.updateAvailableMoves(
                            roomId = it.roomId,
                            selectedIndex = it.selectedIndex,
                            availableMoves = it.availableIndices.toString() // TODO
                        )
                        is TimerUpdateEvent -> gameDao.updateTimer(
                            TimerEntity(
                                roomId = it.roomId,
                                whiteTime = it.whiteTime,
                                blackTime = it.blackTime
                            )
                        )
                    }
                } else {
                    emit(it)
                }
            }


    private fun observeDesiredGameEntities() =
        gameDao.observeJoinedGamesList()
            .distinctUntilChanged()
}