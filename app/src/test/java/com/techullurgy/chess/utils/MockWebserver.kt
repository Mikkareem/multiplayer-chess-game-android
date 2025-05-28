package com.techullurgy.chess.utils

import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MockWebserver {
    var incomingChannel: Channel<Frame> = Channel()
        private set
    var outgoingChannel: Channel<Frame> = Channel()
        private set

    private val mockings = mutableListOf<Mocking>()

    fun addMocking(vararg mocking: Mocking) {
        mocking.forEach { mockings.add(it) }
    }

    suspend fun start() {
        coroutineScope {
            launch {
                incomingChannel.consumeEach {
                    it as Frame.Text
                    val received = it.readText()
                    mockings.firstOrNull { it.predicate(received) }?.let {
                        outgoingChannel.send(Frame.Text(it.output))
                    }
                }
            }
        }
    }

    suspend fun receive(frame: Frame) {
        incomingChannel.send(frame)
    }

    fun isActive() = !incomingChannel.isClosedForReceive || !outgoingChannel.isClosedForReceive

    fun stop() {
        mockings.clear()
        incomingChannel.close()
        outgoingChannel.close()
    }

    fun reset() {
        mockings.clear()
        incomingChannel = Channel()
        outgoingChannel = Channel()
    }
}

data class Mocking(
    val output: String,
    val predicate: (String) -> Boolean,
)