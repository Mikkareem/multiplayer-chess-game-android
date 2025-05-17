package com.techullurgy.chess.presentation.create_new_game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techullurgy.chess.presentation.app.AppViewModel
import com.techullurgy.chess.presentation.utils.koinActivityViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateNewGameScreenRoot(
    onBackClick: () -> Unit
) {
    val viewModel = koinViewModel<CreateNewGameViewModel>()
    val appViewModel = koinActivityViewModel<AppViewModel>()

    CreateNewGameScreen(
        message = "",
        onBackClick = onBackClick
    )
}

@Composable
fun CreateNewGameScreen(
    message: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message)
            Button(onBackClick) { Text("Back") }
        }
    }
}