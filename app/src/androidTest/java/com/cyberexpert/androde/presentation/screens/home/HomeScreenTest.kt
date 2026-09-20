package com.cyberexpert.androde.presentation.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cyberexpert.androde.presentation.components.ErrorView
import com.cyberexpert.androde.presentation.components.LoadingView
import com.cyberexpert.androde.presentation.theme.AndroidAppTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingView_displaysProgress() {
        composeTestRule.setContent {
            AndroidAppTheme {
                LoadingView()
            }
        }
        // CircularProgressIndicator exists - no text, but we can check it doesn't crash
        // For more robust check, we would use semantics
    }

    @Test
    fun errorView_displaysMessageAndRetry() {
        val errorMessage = "Something went wrong"
        composeTestRule.setContent {
            AndroidAppTheme {
                ErrorView(message = errorMessage, onRetry = {})
            }
        }
        composeTestRule.onNodeWithText(errorMessage).assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }
}
