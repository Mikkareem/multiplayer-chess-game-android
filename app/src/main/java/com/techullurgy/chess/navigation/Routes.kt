package com.techullurgy.chess.navigation

import kotlinx.serialization.Serializable

@Serializable
data object Home

@Serializable
data object CreateNewGame

@Serializable
data object JoinExistingGame

@Serializable
data object JoinedGamesList

@Serializable
data class GameRoom(val roomId: String)