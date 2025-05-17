package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import com.techullurgy.chess.domain.PieceColor
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_CELL_SELECTION)
data class CellSelection(
    val roomId: String,
    val color: PieceColor,
    val selectedIndex: Int
): ReceiverBaseEvent