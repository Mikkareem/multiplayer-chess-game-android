package com.techullurgy.chess.presentation.home

import androidx.lifecycle.ViewModel

class HomeViewModel: ViewModel() {
    fun onAction(action: HomeAction) {
        when(action) {
            is HomeAction.OnCreateNewGameClick -> action.handler()
            is HomeAction.OnJoinExistingGameClick -> action.handler()
            is HomeAction.OnQuitClick -> action.handler()
        }
    }
}

sealed interface HomeAction {
    data class OnCreateNewGameClick(val handler: () -> Unit): HomeAction
    data class OnJoinExistingGameClick(val handler: () -> Unit): HomeAction
    data class OnQuitClick(val handler: () -> Unit): HomeAction
}