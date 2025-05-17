package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_DISCONNECT)
data class Disconnected(
    val roomId: String,
): ReceiverBaseEvent