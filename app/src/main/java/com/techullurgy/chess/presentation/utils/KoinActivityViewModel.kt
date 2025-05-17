package com.techullurgy.chess.presentation.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.currentKoinScope
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope
import org.koin.viewmodel.defaultExtras

@Composable
inline fun <reified T: ViewModel> koinActivityViewModel(
    qualifier: Qualifier? = null,
    viewModelStoreOwner: ViewModelStoreOwner = LocalActivity.current as ComponentActivity,
    key: String? = null,
    extras: CreationExtras = defaultExtras(viewModelStoreOwner),
    scope: Scope = currentKoinScope(),
    noinline parameters: ParametersDefinition? = null
): T = koinViewModel<T>(
    qualifier,
    viewModelStoreOwner,
    key,
    extras,
    scope,
    parameters
)