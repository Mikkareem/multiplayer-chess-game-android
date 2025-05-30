package com.techullurgy.chess.apitest

import androidx.test.platform.app.InstrumentationRegistry
import app.cash.turbine.test
import com.techullurgy.chess.data.GameRepositoryImpl
import com.techullurgy.chess.data.dto.GameRoomDto
import com.techullurgy.chess.data.events.CellSelection
import com.techullurgy.chess.domain.JoinedGameState
import com.techullurgy.chess.domain.NetworkLoadingState
import com.techullurgy.chess.domain.PieceColor
import com.techullurgy.chess.utils.EmbeddedServiceRule
import com.techullurgy.chess.utils.Mocking
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
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
    internal val rule = EmbeddedServiceRule(context)

    @Test
    fun `we should not connect to websocket when no joined games are available`() = runBlocking {
        launch { rule.start() }

        rule.addMockings(
            Mocking("Good Afternoon Irsath 1") { it is CellSelection && it.selectedIndex == 3 },
            Mocking("Good Afternoon Irsath 2") { it is CellSelection && it.selectedIndex == 4 },
            Mocking("Good Afternoon Irsath 3") { it is CellSelection && it.selectedIndex == 5 },
            Mocking("Good Afternoon Irsath 4") { it is CellSelection && it.selectedIndex == 6 },
            Mocking("Good Afternoon Irsath 5") { it is CellSelection && it.selectedIndex == 7 },
        )

        val session = HttpClient().webSocketSession("ws://localhost:8083/join/ws")

        val eventsDeferred = async {
            session.incoming.consumeAsFlow().toList().map {
                (it as Frame.Text).readText()
            }
        }

        rule.sendEventToServer(CellSelection("123", PieceColor.White, 3))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 4))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 5))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 6))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 7))

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
//            Mocking("Good Afternoon Irsath 1") { it is CellSelection && it.selectedIndex == 3 },
            Mocking("Good Afternoon Irsath 2") { it is CellSelection && it.selectedIndex == 4 },
            Mocking("Good Afternoon Irsath 3") { it is CellSelection && it.selectedIndex == 5 },
            Mocking("Good Afternoon Irsath 4") { it is CellSelection && it.selectedIndex == 6 },
            Mocking("Good Afternoon Irsath 5") { it is CellSelection && it.selectedIndex == 7 },
        )

        val session = HttpClient().webSocketSession("ws://localhost:8083/join/ws")

        val eventsDeferred = async {
            session.incoming.consumeAsFlow().toList().map {
                (it as Frame.Text).readText()
            }
        }

        rule.sendEventToServer(CellSelection("123", PieceColor.White, 3))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 4))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 5))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 6))
        rule.sendEventToServer(CellSelection("123", PieceColor.White, 7))

        delay(10)

        session.close()

        val events = eventsDeferred.await()

        assertEquals(
            List(4) { "Good Afternoon Irsath ${it+2}" },
            events
        )
    }

    @Test
    fun `joined game working fine`(): Unit = runBlocking {
        coEvery { rule.gameApi.fetchAnyJoinedRoomsAvailable() } returns listOf(GameRoomDto("123", "Test Room", "Test Room Description", listOf()))
        every { rule.gameApi.isSocketActive } returns false

        val repository = GameRepositoryImpl(rule.broker, rule.gameApi, rule.gameDao, CoroutineScope(Job()))

        repository.getJoinedGame("123").test {
            val emission1 = awaitItem()
            println(emission1)
            assertEquals(NetworkLoadingState, emission1)
            val emission2 = awaitItem()
            println(emission2)
            assertEquals(true, emission2 is JoinedGameState)
            cancelAndIgnoreRemainingEvents()
            awaitComplete()
        }

        delay(6000)
        coVerify(atLeast = 2) { rule.gameDao.invalidateJoinedRooms() }
        assertEquals(0, rule.gameDao.gameEntityCount())
    }
}