package com.cyberexpert.androde

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.cyberexpert.androde.presentation.navigation.AppNavGraph
import com.cyberexpert.androde.presentation.theme.AndrodeTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Androde - VS Code like IDE for Android
 * Single Activity hosting Compose Navigation.
 * Edge-to-edge enforced for modern IDE experience.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndrodeTheme {
                AppNavGraph()
            }
        }
    }
}
