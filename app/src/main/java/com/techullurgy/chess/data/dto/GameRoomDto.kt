package com.techullurgy.chess.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameRoomDto(
    val roomId: String,
    val roomName: String,
    val members: List<String>
)
