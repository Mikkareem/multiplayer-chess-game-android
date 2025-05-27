package com.techullurgy.chess.domain.events

import com.techullurgy.chess.domain.PieceColor

sealed interface GameEvent

sealed interface ServerGameEvent: GameEvent
sealed interface ClientGameEvent: GameEvent
sealed interface NetworkRelatedEvent: GameEvent
sealed interface UnknownEvent: GameEvent

data class GameLoadingEvent(
    val roomId: String
): ServerGameEvent

data class ResetSelectionDoneEvent(
    val roomId: String
): ServerGameEvent

data class SelectionResultEvent(
    val roomId: String,
    val availableIndices: List<Int>,
    val selectedIndex: Int
): ServerGameEvent

data class TimerUpdateEvent(
    val roomId: String,
    val whiteTime: Long,
    val blackTime: Long
): ServerGameEvent

data class GameUpdateEvent(
    val roomId: String,
    val board: String,
    val currentTurn: PieceColor,
    val lastMove: String?,
    val cutPieces: String?,
    val kingInCheckIndex: Int?,
    val gameOver: Boolean
): ServerGameEvent

data class CellSelectionEvent(
    val roomId: String,
    val color: PieceColor,
    val selectedIndex: Int
): ClientGameEvent

data class DisconnectedEvent(
    val roomId: String
): ClientGameEvent

data class EnterRoomEvent(
    val roomId: String
): ClientGameEvent

data class PieceMoveEvent(
    val roomId: String,
    val color: PieceColor,
    val from: Int,
    val to: Int
): ClientGameEvent

data class ResetSelectionEvent(
    val roomId: String
): ClientGameEvent

data object NetworkNotAvailableEvent: NetworkRelatedEvent
data object UserDisconnectedEvent: NetworkRelatedEvent
data object GameNotAvailableEvent: UnknownEvent
data object NotYetAnyEventAvailableEvent: UnknownEvent
data object SomethingWentWrongEvent: UnknownEvent