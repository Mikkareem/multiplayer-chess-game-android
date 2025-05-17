package com.techullurgy.chess

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Test

import org.junit.Assert.*
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun appStartUpWorkAsExpected() {
        Robolectric.buildActivity(MainActivity::class.java).use {
            it.setup()
        }
    }

    @Test
    fun greetingComponentWorkingAsExpected() {
        composeTestRule.waitUntil { true }
        composeTestRule.onNodeWithText("Hello Android!").assertIsDisplayed()
    }
}