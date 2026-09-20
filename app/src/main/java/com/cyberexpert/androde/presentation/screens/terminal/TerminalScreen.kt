package com.cyberexpert.androde.presentation.screens.terminal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.presentation.components.ide.TerminalView

/**
 * Terminal screen, similar to VS Code integrated terminal panel.
 * Features:
 * - Multiple terminal sessions
 * - Shell execution
 * - Clear, new terminal
 */
@Composable
fun TerminalScreen(
    workingDir: String,
    viewModel: TerminalViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Session tabs
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                AssistChip(
                    onClick = { viewModel.setActiveSession(session) },
                    label = { Text(session.name) },
                    trailingIcon = {
                        IconButton(onClick = { viewModel.closeSession(session.id) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                )
            }
            item {
                IconButton(onClick = { viewModel.createSession(workingDir) }) {
                    Icon(Icons.Default.Add, contentDescription = "New Terminal")
                }
            }
        }

        if (activeSession != null) {
            TerminalView(
                session = activeSession,
                onCommand = { cmd -> viewModel.executeCommand(cmd) },
                onClear = { viewModel.clearSession() },
                modifier = Modifier.weight(1f)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No terminal session. Create a new one.",
                    style = MaterialTheme.typography.bodyMedium
                )
                androidx.compose.material3.Button(
                    onClick = { viewModel.createSession(workingDir) },
                    modifier = Modifier.padding(top = 12.dp)
                ) {
                    Text("New Terminal")
                }
            }
        }
    }
}
