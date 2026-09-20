package com.cyberexpert.androde.presentation.screens.output

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.OutputChannel

/**
 * Output panel, similar to VS Code Output view.
 * Shows logs from extensions, tasks, etc.
 */
@Composable
fun OutputScreen(
    modifier: Modifier = Modifier
) {
    var selectedChannel by remember { mutableStateOf("Androde") }

    val channels = remember {
        listOf(
            OutputChannel(id = "androde", name = "Androde", lines = listOf(
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Androde v1.0.0-androde started"),
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Sora Editor 0.23.6 loaded"),
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "JGit 6.10.0 loaded"),
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Workspace opened: /data/data/com.cyberexpert.androde/files")
            )),
            OutputChannel(id = "git", name = "Git", lines = listOf(
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Git repository detected: .git"),
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Current branch: main")
            )),
            OutputChannel(id = "extensions", name = "Extensions", lines = listOf(
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "5 extensions installed"),
                com.cyberexpert.androde.domain.model.ide.OutputLine(content = "Kotlin, Java, Python, Dracula, Monokai")
            ))
        )
    }

    val activeChannel = channels.find { it.name == selectedChannel } ?: channels.first()

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(channels) { channel ->
                    AssistChip(
                        onClick = { selectedChannel = channel.name },
                        label = { Text(channel.name) }
                    )
                }
            }
            IconButton(onClick = { /* clear */ }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(activeChannel.lines) { line ->
                Text(
                    text = line.content,
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                    modifier = Modifier.padding(vertical = 1.dp)
                )
            }
        }
    }
}
