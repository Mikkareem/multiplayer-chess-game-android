package com.techullurgy.chess.domain.api

import com.techullurgy.chess.data.dto.GameRoomDto
import com.techullurgy.chess.domain.events.ClientGameEvent
import com.techullurgy.chess.domain.events.GameEvent
import kotlinx.coroutines.flow.Flow

interface ChessGameApi {
    val isSocketActive: Boolean

    val gameEventsFlow: Flow<GameEvent>

    fun startSession()
    fun stopSession()

    fun sendEvent(event: ClientGameEvent)

    suspend fun fetchAnyJoinedRoomsAvailable(): List<GameRoomDto>

    companion object {
        private const val HOST_AND_PORT = "192.168.225.184:8080"

        const val HTTP_BASE_URL = "http://$HOST_AND_PORT"
        const val WS_BASE_URL = "ws://$HOST_AND_PORT"
    }
}