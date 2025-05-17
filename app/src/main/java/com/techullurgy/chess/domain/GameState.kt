package com.techullurgy.chess.domain

data class GameState(
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
)

data class GameStateHeader(
    val roomId: String = "",
    val roomName: String = "",
    val membersCount: Int = 0,
    val isMyTurn: Boolean = false,
    val yourTime: Long = 0,
    val opponentTime: Long = 0,
)