package com.techullurgy.chess.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.techullurgy.chess.domain.GameStatus
import com.techullurgy.chess.data.db.projections.JoinedGameEntityHeaderProjection
import com.techullurgy.chess.data.db.projections.JoinedGameEntityProjection
import com.techullurgy.chess.domain.PieceColor
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("""
        SELECT g.roomId, 
                g.roomName, 
                g.isMyTurn,
                2 as membersCount,
                g.status 
        FROM GameEntity g
        JOIN TimerEntity t
            ON (g.roomId == t.roomId)
        WHERE status IN (:statuses)
    """)
    fun observeJoinedGamesList(
        statuses: List<GameStatus> = listOf(GameStatus.Ongoing, GameStatus.Joined)
    ): Flow<List<JoinedGameEntityHeaderProjection>>

    @Query("""
        SELECT g.*, t.* 
        FROM GameEntity g
        JOIN TimerEntity t
            ON (g.roomId == t.roomId)
        WHERE roomId = :roomId AND status IN (:statuses)
    """)
    fun observeJoinedGame(
        roomId: String,
        statuses: List<GameStatus> = listOf(GameStatus.Ongoing, GameStatus.Joined)
    ): Flow<JoinedGameEntityProjection>

    @Query("""
        SELECT * FROM TimerEntity WHERE roomId = :roomId
    """)
    fun observeTimerFor(roomId: String): Flow<TimerEntity>

    @Query("""
        UPDATE GameEntity
        SET board = :board,
            lastMove = :lastMove,
            isMyTurn = :isMyTurn,
            cutPieces = :cutPieces,
            kingInCheckIndex = :kingInCheckIndex,
            gameOver = :gameOver,
            availableMoves = "",
            selectedIndex = -1
        WHERE roomId = :roomId
    """)
    suspend fun updateGame(
        roomId: String,
        board: String,
        lastMove: String?,
        isMyTurn: Boolean,
        cutPieces: String?,
        kingInCheckIndex: Int?,
        gameOver: Boolean
    )

    @Query(
        """
            UPDATE GameEntity
            SET availableMoves = :availableMoves, 
                selectedIndex = :selectedIndex
            WHERE roomId = :roomId
        """
    )
    suspend fun updateAvailableMoves(
        roomId: String,
        selectedIndex: Int,
        availableMoves: String
    )

    @Query(
        """
            UPDATE GameEntity
            SET availableMoves = "", 
                selectedIndex = -1
            WHERE roomId = :roomId
        """
    )
    suspend fun resetSelection(
        roomId: String
    )

    @Query("SELECT assignedColor FROM GameEntity WHERE roomId = :roomId")
    suspend fun getAssignedColor(roomId: String): PieceColor

    @Upsert
    suspend fun updateTimer(timer: TimerEntity)
}