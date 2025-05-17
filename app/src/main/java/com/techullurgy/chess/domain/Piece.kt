package com.techullurgy.chess.domain

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.techullurgy.chess.R

sealed class Piece(
    @DrawableRes private val resId: Int,
    val pieceColor: PieceColor
) {
    val content = @Stable @Composable {
        Icon(
            modifier = Modifier.fillMaxSize(0.8f),
            painter = painterResource(resId),
            contentDescription = "",
            tint = when(pieceColor) {
                PieceColor.Black -> Color.Black
                PieceColor.White -> Color.White
            }
        )
    }
}

data class Pawn(
    val color: PieceColor
): Piece(R.drawable.pawn, color)

data class King(
    val color: PieceColor
): Piece(R.drawable.king, color)

data class Queen(
    val color: PieceColor
): Piece(R.drawable.queen, color)

data class Bishop(
    val color: PieceColor
): Piece(R.drawable.bishop, color)

data class Knight(
    val color: PieceColor
): Piece(R.drawable.knight, color)

data class Rook(
    val color: PieceColor
): Piece(R.drawable.rook, color)