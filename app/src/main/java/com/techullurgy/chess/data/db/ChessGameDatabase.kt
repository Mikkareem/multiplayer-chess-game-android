package com.techullurgy.chess.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameEntity::class, TimerEntity::class],
    version = 1
)
abstract class ChessGameDatabase: RoomDatabase() {
    abstract val gameDao: GameDao
}