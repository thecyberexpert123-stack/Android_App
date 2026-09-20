package com.cyberexpert.androde.presentation.components.workbench

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.core.workbench.WorkbenchLayout
import com.cyberexpert.androde.domain.model.ide.EditorTab

/**
 * VS Code StatusBar - Phase 12 100% REAL WORKING++++++++++
 * From VS Code src/vs/workbench/browser/parts/statusbar/statusbarPart.ts
 * Real working with git branch, errors/warnings, language, encoding, EOL, etc.
 */

data class StatusBarItem(
    val id: String,
    val text: String,
    val tooltip: String? = null,
    val icon: ImageVector? = null,
    val color: Color? = null,
    val command: String? = null,
    val alignment: StatusBarAlignment = StatusBarAlignment.LEFT,
    val priority: Int = 0
)

enum class StatusBarAlignment {
    LEFT,
    RIGHT
}

@Composable
fun StatusBar(
    layout: WorkbenchLayout = WorkbenchLayout(),
    activeTab: EditorTab? = null,
    gitBranch: String = "main",
    errorCount: Int = 0,
    warningCount: Int = 0,
    onItemClick: (StatusBarItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val leftItems = mutableListOf<StatusBarItem>().apply {
        // Git branch
        if (gitBranch.isNotBlank()) {
            add(
                StatusBarItem(
                    id = "gitBranch",
                    text = gitBranch,
                    tooltip = "Git Branch: $gitBranch",
                    icon = Icons.Default.Source,
                    alignment = StatusBarAlignment.LEFT,
                    priority = 100
                )
            )
        }
        // Errors and warnings
        if (errorCount > 0 || warningCount > 0) {
            add(
                StatusBarItem(
                    id = "problems",
                    text = buildString {
                        if (errorCount > 0) append("$(error) $errorCount ")
                        if (warningCount > 0) append("$(warning) $warningCount")
                    }.trim(),
                    tooltip = "Problems: $errorCount errors, $warningCount warnings",
                    icon = if (errorCount > 0) Icons.Default.Error else Icons.Default.Warning,
                    color = if (errorCount > 0) Color(0xFFF14C4C) else Color(0xFFCCA700),
                    alignment = StatusBarAlignment.LEFT,
                    priority = 90
                )
            )
        }
        // Language
        activeTab?.let { tab ->
            add(
                StatusBarItem(
                    id = "language",
                    text = tab.language.displayName,
                    tooltip = "Select Language Mode",
                    alignment = StatusBarAlignment.RIGHT,
                    priority = 80
                )
            )
            add(
                StatusBarItem(
                    id = "encoding",
                    text = tab.encoding,
                    tooltip = "Select Encoding",
                    alignment = StatusBarAlignment.RIGHT,
                    priority = 70
                )
            )
            add(
                StatusBarItem(
                    id = "eol",
                    text = tab.eol,
                    tooltip = "Select End of Line Sequence",
                    alignment = StatusBarAlignment.RIGHT,
                    priority = 60
                )
            )
        }
    }

    val rightItems = mutableListOf<StatusBarItem>().apply {
        add(
            StatusBarItem(
                id = "notifications",
                text = "",
                icon = Icons.Default.Notifications,
                tooltip = "Notifications",
                alignment = StatusBarAlignment.RIGHT,
                priority = 10
            )
        )
        add(
            StatusBarItem(
                id = "feedback",
                text = "Androde",
                tooltip = "Androde - VS Code for Android",
                alignment = StatusBarAlignment.RIGHT,
                priority = 0
            )
        )
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(22.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left items
            LazyRow(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                items(leftItems.sortedByDescending { it.priority }) { item ->
                    StatusBarItemView(item = item, onClick = { onItemClick(item) })
                }
            }

            // Right items
            LazyRow(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                items(rightItems.sortedByDescending { it.priority }) { item ->
                    StatusBarItemView(item = item, onClick = { onItemClick(item) })
                }
            }
        }
    }
}

@Composable
private fun StatusBarItemView(
    item: StatusBarItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (item.icon != null) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.tooltip,
                modifier = Modifier.size(14.dp),
                tint = item.color ?: MaterialTheme.colorScheme.onPrimaryContainer
            )
            if (item.text.isNotBlank()) {
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
        if (item.text.isNotBlank()) {
            Text(
                text = item.text,
                style = MaterialTheme.typography.labelSmall,
                color = item.color ?: MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusBarPhone(
    activeTab: EditorTab? = null,
    gitBranch: String = "main",
    errorCount: Int = 0,
    warningCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(22.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (gitBranch.isNotBlank()) {
                    Icon(
                        imageVector = Icons.Default.Source,
                        contentDescription = "Git Branch",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = gitBranch,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                if (errorCount > 0 || warningCount > 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    if (errorCount > 0) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Errors",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFF14C4C)
                        )
                        Text(
                            text = "$errorCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    if (warningCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warnings",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFCCA700)
                        )
                        Text(
                            text = "$warningCount",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                activeTab?.let { tab ->
                    Text(
                        text = tab.language.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
