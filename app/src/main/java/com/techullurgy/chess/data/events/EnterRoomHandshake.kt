package com.techullurgy.chess.data.events

import com.techullurgy.chess.data.events.constants.BaseEventConstants
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName(BaseEventConstants.TYPE_ENTER_ROOM_HANDSHAKE)
data class EnterRoomHandshake(
    val roomId: String,
): ReceiverBaseEvent