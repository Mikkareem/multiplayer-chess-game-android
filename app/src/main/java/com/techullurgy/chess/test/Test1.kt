package com.techullurgy.chess.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.IOException

private fun getChannelFlow(): Flow<Int> = channelFlow<Int> {
    launch {
        (1..1000).asFlow().onEach {
            delay(1000)
            send(it)
            if(it % 200 == 0) throw IOException("% by 20")
        }.launchIn(this)
    }

    awaitClose {
        println("Channel flow closed")
    }
}
    .retry(6) { it is IOException }
    .catch {
        println("Catch Called")
        emit(Int.MAX_VALUE)
    }

private fun <T> Flow<T>.asShared(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(3000)
): SharedFlow<T> = shareIn(scope, sharingStarted)


@OptIn(ExperimentalCoroutinesApi::class)
fun main(): Unit = runBlocking {
    val canStart = MutableStateFlow<Boolean>(true)
    val sharedFlow = canStart
        .flatMapLatest {
            if(it) getChannelFlow()
            else flow {}
        }

    delay(3000)

    val collectionJob = launch {
        sharedFlow.collect {
            println(it)
        }
    }

    delay(6000)
    canStart.value = false
    delay(6000)

    println("Program Ended")

    delay(5000)
}