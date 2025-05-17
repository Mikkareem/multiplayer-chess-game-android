package com.techullurgy.chess.presentation.home

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.techullurgy.chess.presentation.app.AppViewModel
import com.techullurgy.chess.presentation.utils.koinActivityViewModel
import com.techullurgy.chess.ui.theme.OnlineChessTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreenRoot(
    onCreateNewGameClick: () -> Unit,
    onJoinExistingGameClick: () -> Unit,
    onQuitClick: () -> Unit
) {
    val viewModel = koinViewModel<HomeViewModel>()
    val appViewModel = koinActivityViewModel<AppViewModel>()

    val onCreateNewGameClick = { viewModel.onAction(HomeAction.OnCreateNewGameClick(onCreateNewGameClick)) }
    val onJoinExistingGameClick = { viewModel.onAction(HomeAction.OnJoinExistingGameClick(onJoinExistingGameClick)) }
    val onQuitClick = { viewModel.onAction(HomeAction.OnQuitClick(onQuitClick)) }

    HomeScreen(
        message = "",
        onCreateNewGameClick = onCreateNewGameClick,
        onJoinExistingGameClick = onJoinExistingGameClick,
        onQuitClick = onQuitClick
    )
}

@Composable
internal fun HomeScreen(
    message: String,
    onCreateNewGameClick: () -> Unit,
    onJoinExistingGameClick: () -> Unit,
    onQuitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(message)
            Button(onCreateNewGameClick) {
                Text("Create New Game")
            }
            Button(onJoinExistingGameClick) {
                Text("Join Existing Game")
            }
            Button(onQuitClick) {
                Text("Quit")
            }
        }
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    OnlineChessTheme {
        HomeScreen("", {}, {}, {})
    }
}