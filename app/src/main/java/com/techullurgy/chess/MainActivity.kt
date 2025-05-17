package com.techullurgy.chess

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.techullurgy.chess.navigation.NavigationRoot
import com.techullurgy.chess.ui.theme.OnlineChessTheme

class MainActivity : ComponentActivity() {

//    private val _vm: AppViewModel by viewModel<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OnlineChessTheme {
                NavigationRoot()
            }
        }
    }
}