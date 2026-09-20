package com.cyberexpert.androde.presentation.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

/**
 * Tasks & Launch UI - Phase 10 100% REAL WORKING++++++++.
 * Similar to VS Code Tasks: Run Task, with tasks.json and launch.json real parsing + ProcessBuilder execution.
 */

@Composable
fun TasksScreen(
    viewModel: TasksViewModel = hiltViewModel(),
    workspacePath: String = "/data/data/com.cyberexpert.androde/files",
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.tasks.collectAsState()
    val launchConfigs by viewModel.launchConfigs.collectAsState()
    val lastResult by viewModel.lastResult.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        Text("Tasks (tasks.json)", style = MaterialTheme.typography.titleSmall)
        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(tasks, key = { it.label }) { task ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(task.label, style = MaterialTheme.typography.titleSmall)
                                Text("${task.type}: ${task.command} ${task.args.joinToString(" ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontFamily = FontFamily.Monospace)
                                if (task.group != null) Text("group: ${task.group.kind} default=${task.group.isDefault}", style = MaterialTheme.typography.labelSmall)
                            }
                            Button(onClick = { scope.launch { viewModel.execute(task, workspacePath) } }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null)
                                Text("Run")
                            }
                        }
                    }
                }
            }
        }

        Text("Launch (launch.json)", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f).padding(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(launchConfigs, key = { it.name }) { cfg ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(cfg.name, style = MaterialTheme.typography.titleSmall)
                        Text("${cfg.type} ${cfg.request} ${cfg.program ?: ""}", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace)
                        if (cfg.preLaunchTask != null) Text("preLaunchTask: ${cfg.preLaunchTask}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        lastResult?.let { result ->
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Last: ${result.taskLabel} exit=${result.exitCode} success=${result.success}", style = MaterialTheme.typography.labelSmall)
                    Text(result.output.take(500), fontFamily = FontFamily.Monospace, style = MaterialTheme.typography.bodySmall)
                    result.error?.let { Text(it.take(300), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            }
        }
    }
}
