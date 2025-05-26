package com.techullurgy.chess.domain

sealed interface GameState

data class JoinedGameState(
    val roomId: String,
    val roomName: String,
    val members: List<String>,
    val board: List<Piece?>,
    val assignedColor: PieceColor,
    val lastMove: Pair<Int, Int>? = null,
    val isMyTurn: Boolean = false,
    val availableMoves: List<Int> = emptyList(),
    val cutPieces: List<Piece> = emptyList(),
    val yourTime: Long = 0,
    val opponentTime: Long = 0,
): GameState

data class JoinedGameStateHeader(
    val roomId: String = "",
    val roomName: String = "",
    val membersCount: Int = 0,
    val isMyTurn: Boolean = false,
    val yourTime: Long = 0,
    val opponentTime: Long = 0,
)

data object JoinedGameLoadingState: GameState