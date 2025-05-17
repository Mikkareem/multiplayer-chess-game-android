package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import com.techullurgy.chess.domain.PieceColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_PIECE_MOVE)
data class PieceMove(
    val roomId: String,
    val color: PieceColor,
    val from: Int,
    val to: Int
): ReceiverBaseEvent