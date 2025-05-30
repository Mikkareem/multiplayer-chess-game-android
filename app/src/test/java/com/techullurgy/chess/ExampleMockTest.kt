package com.techullurgy.chess

import com.techullurgy.chess.utils.setPrivateField
import io.mockk.spyk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ExampleMockTest {

    private val testInterface = spyk<TestInterfaceImpl1>()

    @Test
    fun basicTest() {
        testInterface.setPrivateField("anotherString", flowOf(1029, 9012))

        runBlocking {
            val result = testInterface.implement()
            assertEquals(listOf("OriginalFake1","MockedAnotherString", "1029", "9012"), result)
        }
    }
}

interface TestInterface {
    val eventFlow: Flow<Int>
}

class TestInterfaceImpl1: TestInterface {

    override val eventFlow: Flow<Int> = flowOf(89128, 782378)

    private val anotherString = (1..1000).asFlow()

    suspend fun implement(): List<String> {
        return listOf("OriginalFake1", "MockedAnotherString") + anotherString.toList().map { "$it" } + eventFlow.toList().map { "$it" }
    }
}