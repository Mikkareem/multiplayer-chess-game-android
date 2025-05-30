package com.techullurgy.chess.utils

import android.content.Context
import androidx.room.Room
import com.techullurgy.chess.data.ChessGameApiImpl
import com.techullurgy.chess.data.GameRoomMessageBroker
import com.techullurgy.chess.data.db.ChessGameDatabase
import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.data.events.ReceiverBaseEvent
import com.techullurgy.chess.data.events.serializers.receiverBaseEventJson
import com.techullurgy.chess.domain.api.ChessGameApi
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

internal class EmbeddedServiceRule(
    private val context: Context
): TestRule {

    private lateinit var database: ChessGameDatabase
    lateinit var gameDao: GameDao private set

    lateinit var gameApi: ChessGameApi private set
    lateinit var broker: GameRoomMessageBroker private set

    private val mockServer = MockWebserver()

    init {
        clearAllMocks()
        mockkStatic("io.ktor.client.plugins.websocket.BuildersKt")
        mockkStatic(CoroutineScope::class)
        mockkStatic("io.ktor.websocket.WebSocketSessionKt")
    }

    private val mockSession = mockk<DefaultClientWebSocketSession>() {
        every { incoming } answers { mockServer.outgoingChannel }
        every { outgoing } answers { mockServer.incomingChannel }
        coEvery { close() } answers { mockServer.stop() }
        every { isActive } answers { mockServer.isActive() }
    }

    override fun apply(
        base: Statement?,
        description: Description?
    ): Statement? {
        return object : Statement() {
            override fun evaluate() {
                before()
                try {
                    base?.evaluate()
                } finally {
                    after()
                }
            }
        }
    }

    private fun before() {
        database = Room.inMemoryDatabaseBuilder(context, ChessGameDatabase::class.java).build()
        gameDao = spyk(database.gameDao)
        gameApi = spyk(ChessGameApiImpl(HttpClient(), HttpClient()))
        broker = spyk(GameRoomMessageBroker(gameDao, gameApi))
        coEvery { any<HttpClient>().webSocketSession(any<String>()) } returns mockSession
    }

    private fun after() {
        database.close()
    }

    fun addMockings(vararg mocking: Mocking) = mockServer.addMocking(*mocking)

    suspend fun start() = mockServer.start()

    suspend fun sendEventToServer(event: ReceiverBaseEvent) {
        val text = receiverBaseEventJson.encodeToString(event)
        mockServer.receive(Frame.Text(text))
    }
}