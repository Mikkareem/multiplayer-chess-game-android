package com.techullurgy.chess.data.db.projections

import com.techullurgy.chess.domain.GameStatus
import com.techullurgy.chess.domain.PieceColor

data class JoinedGameEntityProjection(
    val roomId: String,
    val roomName: String,
    val createdBy: String,
    val board: String,
    val members: String,
    val assignedColor: PieceColor,
    val lastMove: String? = null,
    val isMyTurn: Boolean,
    val cutPieces: String? = null,
    val status: GameStatus,
    val availableMoves: String = "",
    val selectedIndex: Int = -1,
    val gameOver: Boolean = false,
    val kingInCheckIndex: Int? = null,
    val yourTime: Long,
    val opponentTime: Long
)