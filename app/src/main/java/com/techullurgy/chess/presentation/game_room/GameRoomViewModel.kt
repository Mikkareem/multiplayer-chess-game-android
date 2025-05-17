package com.techullurgy.chess.presentation.game_room

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.techullurgy.chess.navigation.GameRoom
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameRoomViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    private val args = savedStateHandle.toRoute<GameRoom>()

    private val _state = MutableStateFlow(GameRoomState(roomId = args.roomId))
    val state = _state.asStateFlow()


}

data class GameRoomState(
    val roomId: String,
    val roomName: String = "",

    val board: List<String?> = emptyList(),
    val isMyTurn: Boolean = false,
    val lastMove: String = "",
    val loading: Boolean = true,

    val currentSelectedCell: String = "",
    val availableMoves: List<String> = emptyList(),
    val kingCheckCell: String = "",
)

sealed interface GameRoomAction {
    data class OnSelectCell(val cell: String): GameRoomAction
    data class OnMoveSelected(val cell: String): GameRoomAction
}