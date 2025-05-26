package com.techullurgy.chess.apitest

import com.techullurgy.chess.domain.PieceColor
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
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

    var eventProducer: suspend ProducerScope<ServerGameEvent>.(roomId: String) -> Unit = {}

    private val sessionConnectionFlow = channelFlow<ServerGameEvent> {
        launch {
            send(GameLoadingEvent("123"))
            delay(3000)
            eventProducer("123")
            sendTestMessages("123")
        }

        awaitClose {}
    }

    private suspend fun ProducerScope<ServerGameEvent>.sendTestMessages(roomId: String) {
        launch {
            var timer = 30*60*60L
            while(timer >= 0) {
                send(TimerUpdateEvent(roomId, timer, timer))
                timer--
            }
        }

        var timer = 30
        while(timer >= 0) {
            send(
                GameUpdateEvent(
                    roomId = roomId,
                    board = "",
                    currentTurn = PieceColor.White,
                    lastMove = "",
                    cutPieces = "",
                    kingInCheckIndex = null,
                    gameOver = false
                )
            )
            timer--
        }
    }

    override val isSocketActive: Boolean
        get() = canStartSession.value

    @OptIn(ExperimentalCoroutinesApi::class)
    override val gameEventsFlow: Flow<GameEvent> = canStartSession
        .flatMapLatest { enabled ->
            if(enabled) sessionConnectionFlow
            else flow {}
        }

    override fun startSession() {
        canStartSession.value = true
    }

    override fun stopSession() {
        canStartSession.value = false
    }
}