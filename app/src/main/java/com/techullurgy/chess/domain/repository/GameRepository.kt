package com.techullurgy.chess.domain.repository

import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.models.GameRoom
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getJoinedGamesList(): Flow<GameState>

    fun getJoinedGame(roomId: String): Flow<GameState>

    suspend fun createRoom(room: GameRoom)
    suspend fun joinRoom(roomId: String)
    suspend fun getCreatedRoomsByMe(): List<GameRoom>

    suspend fun retry()
}