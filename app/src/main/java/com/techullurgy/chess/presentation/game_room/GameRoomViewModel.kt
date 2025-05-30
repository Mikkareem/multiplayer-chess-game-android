package com.techullurgy.chess.presentation.game_room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.techullurgy.chess.domain.GameNotAvailableState
import com.techullurgy.chess.domain.JoinedGameState
import com.techullurgy.chess.domain.NetworkLoadingState
import com.techullurgy.chess.domain.NetworkNotAvailableState
import com.techullurgy.chess.domain.SomethingWentWrongState
import com.techullurgy.chess.domain.UserDisconnectedState
import com.techullurgy.chess.domain.repository.GameRepository
import com.techullurgy.chess.navigation.GameRoom
import com.techullurgy.chess.presentation.models.GameNotAvailableScreenState
import com.techullurgy.chess.presentation.models.GameOngoingScreenState
import com.techullurgy.chess.presentation.models.GameScreenState
import com.techullurgy.chess.presentation.models.NetworkLoadingScreenState
import com.techullurgy.chess.presentation.models.NetworkNotAvailableScreenState
import com.techullurgy.chess.presentation.models.SomethingWentWrongScreenState
import com.techullurgy.chess.presentation.models.UserDisconnectedScreenState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameRoomViewModel(
    private val gameRepository: GameRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val args = savedStateHandle.toRoute<GameRoom>()

    val gameUpdateEvents: StateFlow<GameScreenState> = gameRepository.getJoinedGame(args.roomId)
        .map {
            when(it) {
                GameNotAvailableState -> GameNotAvailableScreenState
                is JoinedGameState -> GameOngoingScreenState(state = it)
                NetworkLoadingState -> NetworkLoadingScreenState
                NetworkNotAvailableState -> NetworkNotAvailableScreenState
                SomethingWentWrongState -> SomethingWentWrongScreenState
                UserDisconnectedState -> UserDisconnectedScreenState
                else -> TODO()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkLoadingScreenState
        )

    fun onAction(action: GameRoomAction) {
        when(action) {
            GameRoomAction.OnDrawConfirmed -> TODO()
            GameRoomAction.OnMoveConfirmed -> TODO()
            is GameRoomAction.OnMoveSelected -> TODO()
            GameRoomAction.OnResignConfirmed -> TODO()
            is GameRoomAction.OnSelectCell -> TODO()
            GameRoomAction.OnRetry -> viewModelScope.launch { gameRepository.retry() }
        }
    }
}

sealed interface GameRoomAction {
    data class OnSelectCell(val cell: Int): GameRoomAction
    data class OnMoveSelected(val cell: Int): GameRoomAction
    data object OnMoveConfirmed: GameRoomAction
    data object OnResignConfirmed: GameRoomAction
    data object OnDrawConfirmed: GameRoomAction
    data object OnRetry: GameRoomAction
}