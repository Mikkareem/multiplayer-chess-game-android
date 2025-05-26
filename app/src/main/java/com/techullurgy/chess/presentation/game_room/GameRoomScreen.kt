package com.techullurgy.chess.presentation.game_room

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techullurgy.chess.R
import com.techullurgy.chess.presentation.game_room.components.ChessBoard
import com.techullurgy.chess.presentation.game_room.components.GameTopSection
import com.techullurgy.chess.ui.theme.OnlineChessTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameRoomScreenRoot() {
    val viewModel = koinViewModel<GameRoomViewModel>()

    GameRoomScreen()
}

@Composable
private fun GameRoomScreen(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(R.drawable.chess_bg),
            contentDescription = "Game Background",
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(
                color = Color.Blue,
                blendMode = BlendMode.Hue
            ),
            modifier = Modifier
                .fillMaxSize()
        )

        ProvideTextStyle(LocalTextStyle.current.copy(color = Color.White)) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(16.dp)
            ) {
                GameTopSection()
                Spacer(Modifier.height(24.dp))
                ChessBoard()
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
private fun GameRoomScreenPreview() {
    OnlineChessTheme {
        GameRoomScreen()
    }
}