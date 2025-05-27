package com.techullurgy.chess.utils

internal inline fun <reified T> T.setPrivateField(name: String, value: Any?) {
    T::class.java.getDeclaredField(name).also {
        it.isAccessible = true
        it.set(this, value)
    }
}