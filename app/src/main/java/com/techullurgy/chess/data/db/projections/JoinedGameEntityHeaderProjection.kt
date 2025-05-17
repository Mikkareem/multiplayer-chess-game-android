package com.techullurgy.chess.data.db.projections

import com.techullurgy.chess.domain.GameStatus

data class JoinedGameEntityHeaderProjection(
    val roomId: String,
    val roomName: String,
    val membersCount: Int,
    val isMyTurn: Boolean,
    val status: GameStatus,
    val yourTime: Long,
    val opponentTime: Long
)
