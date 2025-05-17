package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_SELECTION_RESULT)
data class SelectionResult(
    val roomId: String,
    val availableIndices: List<Int>,
    val selectedIndex: Int
): SenderBaseEvent