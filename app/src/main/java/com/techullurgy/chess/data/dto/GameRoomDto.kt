package com.techullurgy.chess.data.dto

import com.techullurgy.chess.domain.models.GameRoom
import kotlinx.serialization.Serializable

@Serializable
data class GameRoomDto(
    val roomId: String,
    val roomName: String,
    val roomDescription: String,
    val members: List<String>
)

fun GameRoom.toGameRoomDto(): GameRoomDto = GameRoomDto(
    roomId = roomId,
    roomName = roomName,
    roomDescription = roomDescription,
    members = members
)

fun GameRoomDto.toGameRoom(): GameRoom = GameRoom(
    roomId = roomId,
    roomName = roomName,
    roomDescription = roomDescription,
    members = members
)