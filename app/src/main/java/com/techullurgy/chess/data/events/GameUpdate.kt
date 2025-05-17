package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import com.techullurgy.chess.domain.PieceColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_GAME_UPDATE)
data class GameUpdate(
    val roomId: String,
    val board: String,
    val currentTurn: PieceColor,
    val lastMove: String?,
    val cutPieces: String?,
    val kingInCheckIndex: Int?,
    val gameOver: Boolean
): SenderBaseEvent