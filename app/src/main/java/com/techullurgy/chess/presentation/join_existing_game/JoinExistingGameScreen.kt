package com.techullurgy.chess.presentation.join_existing_game

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
import org.koin.androidx.compose.koinViewModel

@Composable
fun JoinExistingGameScreenRoot(
    onBackClick: () -> Unit
) {
    val viewModel = koinViewModel<JoinExistingGameViewModel>()

    JoinExistingGameScreen(
        oddMessage = "",
        evenMessage = "",
        onBackClick = onBackClick
    )
}

@Composable
internal fun JoinExistingGameScreen(
    oddMessage: String,
    evenMessage: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(oddMessage)
            Text(evenMessage)
            Button(onBackClick) {
                Text("Back")
            }
        }
    }
}