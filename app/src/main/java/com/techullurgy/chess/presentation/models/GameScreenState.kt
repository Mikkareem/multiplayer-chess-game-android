package com.techullurgy.chess.presentation.models

import com.techullurgy.chess.domain.JoinedGameState

sealed interface GameScreenState

data object NetworkLoadingScreenState: GameScreenState
data object NetworkNotAvailableScreenState: GameScreenState
data object SomethingWentWrongScreenState: GameScreenState
data object UserDisconnectedScreenState: GameScreenState

data object GameLoadingScreenState: GameScreenState
data object GameNotAvailableScreenState: GameScreenState

data class GameOngoingScreenState(
    val state: JoinedGameState
): GameScreenState

data class GameCompletedScreenState(
    val result: String
): GameScreenState