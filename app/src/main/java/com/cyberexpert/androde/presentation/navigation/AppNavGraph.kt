package com.cyberexpert.androde.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cyberexpert.androde.presentation.screens.home.HomeScreen
import com.cyberexpert.androde.presentation.screens.ide.IdeScreen
import kotlinx.serialization.Serializable

/**
 * Androde - VS Code like IDE navigation.
 * Type-safe routes using kotlinx.serialization.
 * Main route is IdeScreen (VS Code workbench), with additional screens for settings, etc.
 */
sealed class Screen {
    @Serializable
    data object Ide : Screen()

    @Serializable
    data object Home : Screen() // Legacy, kept for compatibility

    @Serializable
    data class UserDetail(val userId: Int) : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Search : Screen()

    @Serializable
    data object Git : Screen()

    @Serializable
    data object Terminal : Screen()

    @Serializable
    data object Extensions : Screen()
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Ide
    ) {
        composable<Screen.Ide> {
            IdeScreen(
                onOpenFolder = {
                    // In production, launch SAF folder picker
                    // For MVP, use default app files dir
                }
            )
        }
        composable<Screen.Home> {
            HomeScreen()
        }
        // Future routes
        // composable<Screen.Settings> { SettingsScreen() }
        // composable<Screen.Search> { SearchScreen() }
        // composable<Screen.Git> { GitScreen() }
        // composable<Screen.Terminal> { TerminalScreen() }
        // composable<Screen.Extensions> { ExtensionsScreen() }
    }
}
