package com.cyberexpert.androde.presentation.screens.extensionhost

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.core.extensions.RunningExtension

/**
 * Extension Host screen - real working VS Code like extension host with Rhino JS engine.
 * Shows running extensions, activation, deactivation, command execution.
 * Production-ready with real ExtensionHost using Rhino 1.7.14.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionHostScreen(
    viewModel: ExtensionHostViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val runningExtensions by viewModel.runningExtensions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Extension Host - Rhino JS Engine") })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Text(
                text = "Real working extension host with Mozilla Rhino 1.7.14 (pure Java, works on Android). " +
                        "Extensions' activate() called in isolated Rhino context with Androde API (androde.commands, androde.languages, androde.window). " +
                        "Similar to VS Code extension host.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Running: ${runningExtensions.size} extensions",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Text("Loading...", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(runningExtensions, key = { it.extension.id }) { running ->
                        RunningExtensionItem(
                            running = running,
                            onDeactivate = { viewModel.deactivateExtension(running.extension.id) },
                            onExecuteCommand = { cmd -> viewModel.executeCommand(running.extension.id, cmd) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RunningExtensionItem(
    running: RunningExtension,
    onDeactivate: () -> Unit,
    onExecuteCommand: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = running.extension.displayName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = running.extension.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Activated: ${running.isActivated}, Time: ${running.activationTime}, API: ${running.api?.commands?.size ?: 0} commands",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onDeactivate) {
                    Text("Deactivate")
                }
                Button(onClick = { onExecuteCommand("${running.extension.id}.hello") }) {
                    Text("Execute Command")
                }
            }

            running.api?.let { api ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Commands: ${api.commands.joinToString()}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Languages: ${api.languages.joinToString()}",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = "Themes: ${api.themes.joinToString()}",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
