package com.techullurgy.chess.data

import com.techullurgy.chess.data.events.GameLoading
import com.techullurgy.chess.data.events.GameUpdate
import com.techullurgy.chess.data.events.ResetSelectionDone
import com.techullurgy.chess.data.events.SelectionResult
import com.techullurgy.chess.data.events.SenderBaseEvent
import com.techullurgy.chess.data.events.TimerUpdate
import com.techullurgy.chess.data.events.serializers.receiverBaseEventJson
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
import com.techullurgy.chess.domain.events.NetworkNotAvailableEvent
import com.techullurgy.chess.domain.events.ResetSelectionDoneEvent
import com.techullurgy.chess.domain.events.SelectionResultEvent
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class ChessGameApi(
    private val socketClient: HttpClient,
    private val httpClient: HttpClient,
) {
    private var canStartSession = MutableStateFlow(false)

    private var session: DefaultClientWebSocketSession? = null

    val isSocketActive get() = session != null && session!!.isActive

    private val sessionConnectionFlow: Flow<GameEvent> = channelFlow<GameEvent> {
        launch {
            try {
                session = socketClient.webSocketSession(
                    urlString = "$WS_BASE_URL/join/ws"
                )

                session!!.incoming.consumeEach { frame ->
                    if(frame is Frame.Text) {
                        val text = frame.readText()
                        val event = receiverBaseEventJson.decodeFromString<SenderBaseEvent>(text)

                        send(
                            when(event) {
                                is GameLoading -> GameLoadingEvent(event.roomId)
                                is GameUpdate -> GameUpdateEvent(
                                    event.roomId,
                                    event.board,
                                    event.currentTurn,
                                    event.lastMove,
                                    event.cutPieces,
                                    event.kingInCheckIndex,
                                    event.gameOver
                                )
                                is ResetSelectionDone -> ResetSelectionDoneEvent(event.roomId)
                                is SelectionResult -> SelectionResultEvent(
                                    event.roomId,
                                    event.availableIndices,
                                    event.selectedIndex
                                )
                                is TimerUpdate -> TimerUpdateEvent(
                                    roomId = event.roomId,
                                    whiteTime = event.whiteTime,
                                    blackTime = event.blackTime
                                )
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                coroutineContext.ensureActive()
                send(NetworkNotAvailableEvent)
                e.printStackTrace()
                close()
            }
        }

        awaitClose {
            session?.let { s ->
                s.launch {
                    s.close(CloseReason(CloseReason.Codes.NORMAL, "disconnected"))
                }
            }
            session = null
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val gameEventsFlow: Flow<GameEvent> = canStartSession
        .flatMapLatest { enable ->
            if(enable) sessionConnectionFlow
            else flow {}
        }

    fun startSession() {
        canStartSession.value = true
    }

    fun stopSession() {
        canStartSession.value = false
    }

    companion object {
        private const val HOST_AND_PORT = "192.168.225.184:8080"

        private const val HTTP_BASE_URL = "http://$HOST_AND_PORT"
        private const val WS_BASE_URL = "ws://$HOST_AND_PORT"
    }
}