package com.cyberexpert.androde.presentation.screens.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.domain.model.ide.Breakpoint
import com.cyberexpert.androde.domain.model.ide.DebugSession

/**
 * Run & Debug screen, similar to VS Code Run & Debug view (Ctrl+Shift+D).
 * Features:
 * - Debug sessions, breakpoints, variables, call stack
 * - Architecture for DAP (Debug Adapter Protocol) via LSP4J
 */
@Composable
fun DebugScreen(
    viewModel: DebugViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.sessions.collectAsState()
    val breakpoints by viewModel.breakpoints.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.startDebugSession() },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Text("Start Debugging")
            }
            if (activeSession?.isRunning == true) {
                Button(
                    onClick = { activeSession?.let { viewModel.stopSession(it.id) } },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = null)
                    Text("Stop")
                }
            }
        }

        Text(
            text = "Breakpoints (${breakpoints.size})",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(breakpoints, key = { it.id }) { bp ->
                BreakpointItem(breakpoint = bp, onRemove = { viewModel.removeBreakpoint(bp.id) })
            }
        }

        Text(
            text = "Call Stack",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        if (activeSession != null) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(activeSession!!.callStack) { frame ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(frame.name, style = MaterialTheme.typography.bodyMedium)
                            Text("${frame.filePath}:${frame.line}", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        } else {
            Text("No active debug session", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
private fun BreakpointItem(
    breakpoint: Breakpoint,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onRemove,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Icon(Icons.Default.BugReport, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("${breakpoint.filePath.substringAfterLast("/")} : ${breakpoint.line}", style = MaterialTheme.typography.bodySmall)
                if (breakpoint.condition != null) {
                    Text("Condition: ${breakpoint.condition}", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
