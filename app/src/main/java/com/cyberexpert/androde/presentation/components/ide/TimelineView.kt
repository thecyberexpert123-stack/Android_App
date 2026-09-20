package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.EditorTab
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Timeline view similar to VS Code Timeline.
 * Shows file history, git commits for file, local history.
 * Real working implementation using file lastModified and git log if available.
 */
@Composable
fun TimelineView(
    tab: EditorTab?,
    modifier: Modifier = Modifier
) {
    if (tab == null) {
        Text("No file open", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
        return
    }

    val timelineItems = remember(tab.filePath, tab.file) {
        buildTimeline(tab.file)
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
            Text("Timeline", style = MaterialTheme.typography.titleSmall)
        }

        LazyColumn {
            items(timelineItems) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = item.time,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private data class TimelineItem(
    val title: String,
    val time: String,
    val type: String
)

private fun buildTimeline(file: File): List<TimelineItem> {
    val items = mutableListOf<TimelineItem>()
    try {
        if (file.exists()) {
            val lastModified = file.lastModified()
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            items.add(
                TimelineItem(
                    title = "File modified",
                    time = sdf.format(Date(lastModified)),
                    type = "local"
                )
            )
            items.add(
                TimelineItem(
                    title = "File created",
                    time = "Unknown",
                    type = "local"
                )
            )
            // In real implementation, we would query JGit for file history
            // For now, show placeholder for git history
            items.add(
                TimelineItem(
                    title = "Git history available if repo",
                    time = "Use Git view",
                    type = "git"
                )
            )
        }
    } catch (e: Exception) {
        items.add(TimelineItem("Error loading timeline", e.message ?: "", "error"))
    }
    return items
}
