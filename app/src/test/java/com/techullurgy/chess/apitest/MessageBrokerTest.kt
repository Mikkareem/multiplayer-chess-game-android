package com.techullurgy.chess.apitest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.techullurgy.chess.data.GameRepositoryImpl
import com.techullurgy.chess.data.GameRoomMessageBroker
import com.techullurgy.chess.data.db.ChessGameDatabase
import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.domain.events.GameLoadingEvent
import com.techullurgy.chess.domain.events.UserDisconnectedEvent
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MessageBrokerTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var database: ChessGameDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, ChessGameDatabase::class.java).build()
        gameDao = database.gameDao
    }

    @Test
    fun `we should not connect to websocket when no joined games are available`() = runBlocking {
        val gameApi = spyk(FakeChessGameApi()) {
            every { this@spyk.sessionConnectionFlow } returns flow {
                emit(GameLoadingEvent("783"))
                delay(3000)
                emit(UserDisconnectedEvent)
            }
        }
        val gameBroker = GameRoomMessageBroker(gameDao, gameApi)
        val gameRepository = GameRepositoryImpl(gameBroker, gameDao, CoroutineScope(SupervisorJob()))

        val job1 = launch {
            gameRepository.getJoinedGame("123").collect {
                println("State: $it")
            }
        }

        val job2 = launch {
            gameRepository.getJoinedGamesList()
                .collect {
                    println("List: $it")
                }
        }

        verify(exactly = 1) { gameApi.gameEventsFlow }
        delay(4000)
        println("Cancelling")
        assert(gameDao.gameEntityCount() == 1)
        job1.cancelAndJoin()
        job2.cancelAndJoin()

        delay(10000)
        assert(gameDao.gameEntityCount() == 0)
    }

    @After
    fun tearDown() {
        database.close()
    }
}