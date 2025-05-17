package com.techullurgy.chess.di

import com.techullurgy.chess.presentation.app.AppViewModel
import com.techullurgy.chess.presentation.create_new_game.CreateNewGameViewModel
import com.techullurgy.chess.presentation.home.HomeViewModel
import com.techullurgy.chess.presentation.join_existing_game.JoinExistingGameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val UiModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::CreateNewGameViewModel)
    viewModelOf(::JoinExistingGameViewModel)
}