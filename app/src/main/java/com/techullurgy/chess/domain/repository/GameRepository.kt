package com.techullurgy.chess.domain.repository

import com.techullurgy.chess.domain.GameState
import com.techullurgy.chess.domain.GameStateHeader
import com.techullurgy.chess.domain.events.GameEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface GameRepository {
    val updateEvents: SharedFlow<GameEvent>

    fun observeSessions(): Flow<Boolean>

    fun getJoinedGamesList(): Flow<List<GameStateHeader>>

    fun getJoinedGame(roomId: String): Flow<GameState>
}