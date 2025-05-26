package com.techullurgy.chess.apitest

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.techullurgy.chess.data.db.ChessGameDatabase
import com.techullurgy.chess.data.db.GameDao
import com.techullurgy.chess.data.db.TimerEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameDaoTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    private lateinit var database: ChessGameDatabase
    private lateinit var gameDao: GameDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(context, ChessGameDatabase::class.java).build()
        gameDao = database.gameDao
    }

    @Test
    fun basicRoomDatabaseTest_addingItemsToTheRoomSuccessfully() = runBlocking {
        val entries = async {
            gameDao.observeTimerFor("123")
                .filterNotNull()
                .distinctUntilChanged()
                .take(2)
                .toList()
        }

        gameDao.updateTimer(TimerEntity("123", 1, 2))
        gameDao.updateTimer(TimerEntity("728", 4, 9))
        gameDao.updateTimer(TimerEntity("728", 8, 5))
        gameDao.updateTimer(TimerEntity("123", 6, 7))
        gameDao.updateTimer(TimerEntity("123", 12, 89))
        gameDao.updateTimer(TimerEntity("728", 78, 856))

        assertEquals(
            listOf(TimerEntity("123", 1, 2), TimerEntity("123", 6, 7)),
            entries.await()
        )

        assertEquals(2, gameDao.timerEntityCount())
    }


    @After
    fun tearDown() {
        database.close()
    }
}