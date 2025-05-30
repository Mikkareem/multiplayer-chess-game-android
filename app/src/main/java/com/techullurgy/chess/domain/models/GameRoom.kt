package com.techullurgy.chess.domain.models

data class GameRoom(
    val roomId: String,
    val roomName: String,
    val roomDescription: String,
    val members: List<String> = emptyList()
)
