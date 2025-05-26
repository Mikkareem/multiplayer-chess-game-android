package com.techullurgy.chess.presentation.game_room.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.techullurgy.chess.domain.Bishop
import com.techullurgy.chess.domain.King
import com.techullurgy.chess.domain.Knight
import com.techullurgy.chess.domain.Pawn
import com.techullurgy.chess.domain.Piece
import com.techullurgy.chess.domain.PieceColor
import com.techullurgy.chess.domain.Queen
import com.techullurgy.chess.domain.Rook

private val BlackBoardColor = Color(0xff9e6d09)
private val WhiteBoardColor = Color(0xffc9901c)

@Composable
fun ChessBoard(
    modifier: Modifier = Modifier
) {
    val board: List<Piece?> = List(64) {
        when(it) {
            24 -> King(PieceColor.White)
            25 -> Queen(PieceColor.White)
            42 -> Bishop(PieceColor.White)
            43 -> Knight(PieceColor.White)
            44 -> Pawn(PieceColor.White)
            45 -> Rook(PieceColor.White)
            26 -> King(PieceColor.Black)
            27 -> Queen(PieceColor.Black)
            46 -> Bishop(PieceColor.Black)
            47 -> Knight(PieceColor.Black)
            28 -> Pawn(PieceColor.Black)
            29 -> Rook(PieceColor.Black)
            else -> null
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(8),
        modifier = modifier.border(2.dp, Color.Yellow)
    ) {
        items(64) { index ->
            val row = index / 8
            val column = index % 8
            val backgroundColor = if(row % 2 == 0) {
                if(column % 2 == 0) WhiteBoardColor else BlackBoardColor
            } else {
                if(column % 2 == 0) BlackBoardColor else WhiteBoardColor
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                board[index]?.content()
            }
        }
    }
}