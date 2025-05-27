package com.techullurgy.chess.apitest

import com.techullurgy.chess.data.dto.GameRoomDto
import com.techullurgy.chess.domain.PieceColor
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.CellSelectionEvent
import com.techullurgy.chess.domain.events.ClientGameEvent
import com.techullurgy.chess.domain.events.DisconnectedEvent
import com.techullurgy.chess.domain.events.EnterRoomEvent
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.GameNotAvailableEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
import com.techullurgy.chess.domain.events.NetworkNotAvailableEvent
import com.techullurgy.chess.domain.events.PieceMoveEvent
import com.techullurgy.chess.domain.events.ResetSelectionEvent
import com.techullurgy.chess.domain.events.ServerGameEvent
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FakeChessGameApi: ChessGameApi {
    private val canStartSession = MutableStateFlow(false)

    val sessionConnectionFlow = channelFlow<GameEvent> {
        launch {
            send(GameLoadingEvent("172"))
        }

        awaitClose {}
    }

    override val isSocketActive: Boolean
        get() = canStartSession.value

    @OptIn(ExperimentalCoroutinesApi::class)
    override val gameEventsFlow: Flow<GameEvent> get() = canStartSession
        .flatMapLatest { enabled ->
            if(enabled) sessionConnectionFlow
            else flow { emit(GameNotAvailableEvent) }
        }

    override fun startSession() {
        canStartSession.value = true
    }

    override fun stopSession() {
        canStartSession.value = false
    }

    override fun sendEvent(event: ClientGameEvent) {
        when(event) {
            is CellSelectionEvent -> TODO()
            is DisconnectedEvent -> TODO()
            is EnterRoomEvent -> TODO()
            is PieceMoveEvent -> TODO()
            is ResetSelectionEvent -> TODO()
        }
    }

    override suspend fun fetchAnyJoinedRoomsAvailable(): List<GameRoomDto> {
        return listOf(
            GameRoomDto(roomId = "123", roomName = "Test Room", members = listOf("Irsath", "Kareem"))
        )
    }
}