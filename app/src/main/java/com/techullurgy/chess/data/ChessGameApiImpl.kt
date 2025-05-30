package com.techullurgy.chess.data

import com.techullurgy.chess.data.dto.GameRoomDto
import com.techullurgy.chess.data.events.CellSelection
import com.techullurgy.chess.data.events.Disconnected
import com.techullurgy.chess.data.events.EnterRoomHandshake
import com.techullurgy.chess.data.events.GameStarted
import com.techullurgy.chess.data.events.GameUpdate
import com.techullurgy.chess.data.events.PieceMove
import com.techullurgy.chess.data.events.ReceiverBaseEvent
import com.techullurgy.chess.data.events.ResetSelection
import com.techullurgy.chess.data.events.ResetSelectionDone
import com.techullurgy.chess.data.events.SelectionResult
import com.techullurgy.chess.data.events.SenderBaseEvent
import com.techullurgy.chess.data.events.TimerUpdate
import com.techullurgy.chess.data.events.serializers.receiverBaseEventJson
import com.techullurgy.chess.data.events.serializers.senderBaseEventJson
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.CellSelectionEvent
import com.techullurgy.chess.domain.events.ClientGameEvent
import com.techullurgy.chess.domain.events.DisconnectedEvent
import com.techullurgy.chess.domain.events.EnterRoomEvent
import com.techullurgy.chess.domain.events.GameEvent
import com.techullurgy.chess.domain.events.GameStartedEvent
import com.techullurgy.chess.domain.events.GameUpdateEvent
import com.techullurgy.chess.domain.events.NetworkLoadingEvent
import com.techullurgy.chess.domain.events.NetworkNotAvailableEvent
import com.techullurgy.chess.domain.events.PieceMoveEvent
import com.techullurgy.chess.domain.events.ResetSelectionDoneEvent
import com.techullurgy.chess.domain.events.ResetSelectionEvent
import com.techullurgy.chess.domain.events.SelectionResultEvent
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import com.techullurgy.chess.domain.events.UserConnectedEvent
import com.techullurgy.chess.domain.events.UserDisconnectedEvent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.get
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
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

internal class ChessGameApiImpl(
    private val socketClient: HttpClient,
    private val httpClient: HttpClient,
): ChessGameApi {
    private var canStartSession = MutableStateFlow(false)

    private var session: DefaultClientWebSocketSession? = null

    private var eventChannel: Channel<ReceiverBaseEvent> = Channel()

    private val sessionConnectionFlow: Flow<GameEvent> = channelFlow<GameEvent> {
        send(NetworkLoadingEvent)
        launch {
            try {
                try {
                    session = socketClient.webSocketSession(
                        urlString = "${ChessGameApi.WS_BASE_URL}/join/ws"
                    )
                    send(UserConnectedEvent)
                } catch (e: Exception) {
                    send(NetworkNotAvailableEvent)
                    e.printStackTrace()
                    close()
                }

                launch {
                    eventChannel.consumeEach {
                        val text = receiverBaseEventJson.encodeToString(it)
                        val frame = Frame.Text(text)
                        session?.outgoing?.send(frame)
                    }
                }

                session!!.incoming.consumeEach { frame ->
                    if(frame is Frame.Text) {
                        val text = frame.readText()
                        val event = senderBaseEventJson.decodeFromString<SenderBaseEvent>(text)

                        send(
                            when(event) {
                                is GameStarted -> GameStartedEvent(event.roomId)
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
                send(UserDisconnectedEvent)
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
            eventChannel = eventChannel.reset()
        }
    }

    override val isSocketActive get() = session != null && session!!.isActive

    @OptIn(ExperimentalCoroutinesApi::class)
    override val gameEventsFlow: Flow<GameEvent> = canStartSession
        .flatMapLatest { enable ->
            if(enable) sessionConnectionFlow
            else flow {}
        }

    override fun startSession() {
        canStartSession.value = true
    }

    override fun stopSession() {
        canStartSession.value = false
    }

    override fun sendEvent(event: ClientGameEvent) {
        val sendableEvent = when(event) {
            is CellSelectionEvent -> CellSelection(event.roomId, event.color, event.selectedIndex)
            is DisconnectedEvent -> Disconnected(event.roomId)
            is EnterRoomEvent -> EnterRoomHandshake(event.roomId)
            is PieceMoveEvent -> PieceMove(event.roomId, event.color, event.from, event.to)
            is ResetSelectionEvent -> ResetSelection(event.roomId)
        }
        session?.launch {
            eventChannel.send(sendableEvent)
        }
    }

    override suspend fun fetchAnyJoinedRoomsAvailable(): List<GameRoomDto> {
        val response = httpClient.get("${ChessGameApi.HTTP_BASE_URL}/rooms/joined")
        val rooms = response.body<List<GameRoomDto>>()
        return rooms
    }

    override suspend fun getCreatedRoomsByMe(): List<GameRoomDto> {
        TODO("Not yet implemented")
    }

    override suspend fun joinRoom(roomID: String) {
        TODO("Not yet implemented")
    }

    override suspend fun createRoom(room: GameRoomDto) {
        TODO("Not yet implemented")
    }

    private fun <T> Channel<T>.reset(): Channel<T> {
        close()
        return Channel()
    }
}