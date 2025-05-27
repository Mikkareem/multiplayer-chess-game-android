package com.techullurgy.chess.domain.repository

import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.JoinedGameStateHeader
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun getJoinedGamesList(): Flow<GameState>

    fun getJoinedGame(roomId: String): Flow<GameState>
}