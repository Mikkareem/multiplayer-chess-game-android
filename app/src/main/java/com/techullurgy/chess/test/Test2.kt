package com.techullurgy.chess.test

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import org.koin.androidx.compose.koinViewModel

class GlobalViewModelStoreOwner: ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore
        get() = ViewModelStore()
}

enum class Platform {
    Android, IOS, Desktop
}

val currentPlatform = Platform.Android

val LocalGlobalViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner> {
    error("No Viewmodel Store Owner")
}

@Composable
fun getGlobalViewModelStoreOwner(): ViewModelStoreOwner {
    Text("")
    val androidOwner = LocalActivity.current as ComponentActivity
    return remember {
        if(currentPlatform == Platform.Android) {
            androidOwner
        } else {
            GlobalViewModelStoreOwner()
        }
    }
}

class TempViewModel: ViewModel()

@Composable
inline fun <reified T: ViewModel> koinGlobalViewModel(): T {
    return koinViewModel<T>(
        viewModelStoreOwner = LocalGlobalViewModelStoreOwner.current
    )
}

@Composable
fun TempScreen() {
    val viewModelStoreOwner = rememberViewModelStoreOwner()
    CompositionLocalProvider(LocalGlobalViewModelStoreOwner provides viewModelStoreOwner) {
        val tempViewModel = koinGlobalViewModel<TempViewModel>()
    }
}

@Composable
fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val platform = currentPlatform

    val android = LocalActivity.current as ViewModelStoreOwner
    return remember {
        when (platform) {
            Platform.Android -> android
            else -> GlobalViewModelStoreOwner()
        }
    }.also { owner ->
        DisposableEffect(platform) {
            onDispose {
                if (platform != Platform.Android) {
                    owner.viewModelStore.clear()
                }
            }
        }
    }
}

sealed interface AppResult<T>

sealed interface AppNetworkResult<T>: AppResult<T>
sealed interface AppDatabaseResult<T>: AppResult<T>

