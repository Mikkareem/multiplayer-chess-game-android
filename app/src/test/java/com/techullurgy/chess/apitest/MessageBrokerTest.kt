package com.techullurgy.chess.apitest

import androidx.test.platform.app.InstrumentationRegistry
import com.techullurgy.chess.utils.EmbeddedServiceRule
import com.techullurgy.chess.utils.Mocking
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MessageBrokerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @get:Rule
    val rule = EmbeddedServiceRule(context)

    @Test
    fun `we should not connect to websocket when no joined games are available`() = runBlocking {
        launch { rule.start() }

        rule.addMockings(
            Mocking("Good Afternoon Irsath 1") { it == "Good Afternoon: 1" },
            Mocking("Good Afternoon Irsath 2") { it == "Good Afternoon: 2" },
            Mocking("Good Afternoon Irsath 3") { it == "Good Afternoon: 3" },
            Mocking("Good Afternoon Irsath 4") { it == "Good Afternoon: 4" },
            Mocking("Good Afternoon Irsath 5") { it == "Good Afternoon: 5" },
        )

        val session = HttpClient().webSocketSession("ws://localhost:8083/join/ws")

        val eventsDeferred = async {
            session.incoming.consumeAsFlow().toList().map {
                (it as Frame.Text).readText()
            }
        }

        rule.sendEventToServer(Frame.Text("Good Afternoon: 1"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 2"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 3"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 4"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 5"))

        delay(10)

        session.close()

        val events = eventsDeferred.await()

        assertEquals(
            List(5) { "Good Afternoon Irsath ${it+1}" },
            events
        )
    }

    @Test
    fun `we should not connect to websocket when no joined games are not available`() = runBlocking {
        launch { rule.start() }

        rule.addMockings(
//            Mocking("Good Afternoon Irsath 1") { it == "Good Afternoon: 1" },
            Mocking("Good Afternoon Irsath 2") { it == "Good Afternoon: 2" },
            Mocking("Good Afternoon Irsath 3") { it == "Good Afternoon: 3" },
            Mocking("Good Afternoon Irsath 4") { it == "Good Afternoon: 4" },
            Mocking("Good Afternoon Irsath 5") { it == "Good Afternoon: 5" },
        )

        val session = HttpClient().webSocketSession("ws://localhost:8083/join/ws")

        val eventsDeferred = async {
            session.incoming.consumeAsFlow().toList().map {
                (it as Frame.Text).readText()
            }
        }

        rule.sendEventToServer(Frame.Text("Good Afternoon: 1"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 2"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 3"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 4"))
        rule.sendEventToServer(Frame.Text("Good Afternoon: 5"))

        delay(10)

        session.close()

        val events = eventsDeferred.await()

        assertEquals(
            List(4) { "Good Afternoon Irsath ${it+2}" },
            events
        )
    }
}