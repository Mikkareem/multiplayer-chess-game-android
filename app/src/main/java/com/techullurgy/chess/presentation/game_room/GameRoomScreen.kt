package com.techullurgy.chess.presentation.game_room

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel

@Composable
fun GameRoomScreenRoot(
    onBackClick: () -> Unit,
) {
    val viewModel = koinViewModel<GameRoomViewModel>()
}

@Composable
fun GameRoomScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {

        }
    }
}