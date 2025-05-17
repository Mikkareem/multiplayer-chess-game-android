package com.techullurgy.chess.navigation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.techullurgy.chess.presentation.create_new_game.CreateNewGameScreenRoot
import com.techullurgy.chess.presentation.home.HomeScreenRoot
import com.techullurgy.chess.presentation.join_existing_game.JoinExistingGameScreenRoot

@Composable
fun NavigationRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Home
    ) {
        composable<Home> {
            val activity = LocalActivity.current
            HomeScreenRoot(
                onCreateNewGameClick = { navController.navigate(CreateNewGame) },
                onJoinExistingGameClick = { navController.navigate(JoinExistingGame) },
                onQuitClick = { activity?.finishAndRemoveTask() }
            )
        }

        composable<CreateNewGame> {
            CreateNewGameScreenRoot(
                onBackClick =  { navController.popBackStack() }
            )
        }

        composable<JoinExistingGame> {
            JoinExistingGameScreenRoot(
                onBackClick =  { navController.popBackStack() }
            )
        }

        composable<JoinedGamesList> {

        }

        composable<GameRoom>(
            deepLinks = listOf(
                navDeepLink<GameRoom>(
                    basePath = "http://reach-us.com/room"
                )
            )
        ) {
            val (roomId) = it.toRoute<GameRoom>()

            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Given Room id is: $roomId")
            }
        }
    }
}