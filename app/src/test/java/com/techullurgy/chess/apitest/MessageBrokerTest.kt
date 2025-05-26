package com.techullurgy.chess.apitest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.techullurgy.chess.data.GameRepositoryImpl
import com.techullurgy.chess.data.GameRoomMessageBroker
import com.techullurgy.chess.data.db.ChessGameDatabase
import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.data.db.GameEntity
import com.techullurgy.chess.domain.GameStatus
import com.techullurgy.chess.domain.PieceColor
import com.techullurgy.chess.domain.api.ChessGameApi
import com.techullurgy.chess.domain.events.TimerUpdateEvent
import com.techullurgy.chess.domain.repository.GameRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
    private lateinit var gameBroker: GameRoomMessageBroker
    private lateinit var gameRepository: GameRepository
    private lateinit var gameApi: ChessGameApi

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, ChessGameDatabase::class.java).build()
        gameDao = database.gameDao
        gameApi = FakeChessGameApi()
        gameBroker = GameRoomMessageBroker(gameDao, gameApi)
        gameRepository = GameRepositoryImpl(gameBroker, gameDao, CoroutineScope(SupervisorJob()))
    }

    @Test
    fun `we should not connect to websocket when no joined games are available`() = runBlocking {
        (gameApi as FakeChessGameApi).eventProducer = {
            send(TimerUpdateEvent(it, 88, 90))
            delay(1000)
            send(TimerUpdateEvent(it, 89, 90))
        }

        val job = launch {
            gameRepository.getJoinedGame("123")
                .collect {
                    println("Actual $it")
                }
        }

        delay(3000)
        gameDao.insertGame(
            GameEntity(
                roomId = "123",
                roomName = "Googk",
                createdBy = "Irsath",
                board = "",
                members = "",
                assignedColor = PieceColor.White,
                isMyTurn = true,
                status = GameStatus.Joined
            )
        )
        println("Inserted Joined game for 123")

        delay(4000)
        job.cancel()
    }

    @After
    fun tearDown() {
        database.close()
    }
}