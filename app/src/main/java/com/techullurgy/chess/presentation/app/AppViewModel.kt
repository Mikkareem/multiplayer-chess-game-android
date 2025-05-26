package com.techullurgy.chess.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppViewModel: ViewModel() {

    init {
        viewModelScope.launch {
        }.invokeOnCompletion {
        }
    }
}