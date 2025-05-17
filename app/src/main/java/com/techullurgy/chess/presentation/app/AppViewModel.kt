package com.techullurgy.chess.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techullurgy.chess.data.ChessGameApi
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    init {
        viewModelScope.launch {
        }.invokeOnCompletion {
        }
    }
}