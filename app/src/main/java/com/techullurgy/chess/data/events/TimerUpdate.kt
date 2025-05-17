package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_TIMER_UPDATE)
data class TimerUpdate(
    val roomId: String,
    val whiteTime: Long,
    val blackTime: Long,
): SenderBaseEvent