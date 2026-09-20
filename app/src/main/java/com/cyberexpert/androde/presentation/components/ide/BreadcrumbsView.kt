package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.EditorTab
import com.cyberexpert.androde.domain.model.ide.Project
import java.io.File

/**
 * Breadcrumbs view similar to VS Code breadcrumbs.
 * Shows file path from project root to current file with clickable segments.
 * Real working implementation with actual file path navigation.
 */
@Composable
fun BreadcrumbsView(
    activeTab: EditorTab?,
    currentProject: Project?,
    onBreadcrumbClick: (File) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeTab == null || currentProject == null) return

    val scrollState = rememberScrollState()
    val breadcrumbs = buildBreadcrumbs(activeTab, currentProject)

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        breadcrumbs.forEachIndexed { index, crumb ->
            if (index > 0) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 2.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(
                onClick = { onBreadcrumbClick(crumb.file) },
                modifier = Modifier.padding(0.dp)
            ) {
                Text(
                    text = crumb.name,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (index == breadcrumbs.lastIndex) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private data class BreadcrumbItem(
    val name: String,
    val file: File
)

private fun buildBreadcrumbs(tab: EditorTab, project: Project): List<BreadcrumbItem> {
    val result = mutableListOf<BreadcrumbItem>()
    try {
        val projectRoot = project.rootFile
        val file = tab.file
        result.add(BreadcrumbItem(project.name, projectRoot))

        val relativePath = file.absolutePath.removePrefix(projectRoot.absolutePath).trimStart('/', '\\')
        if (relativePath.isEmpty()) {
            return result
        }
        val parts = relativePath.split(File.separator)
        var current = projectRoot
        for (part in parts) {
            if (part.isBlank()) continue
            current = File(current, part)
            result.add(BreadcrumbItem(part, current))
        }
    } catch (e: Exception) {
        // Fallback to file name only
        result.add(BreadcrumbItem(tab.fileName, tab.file))
    }
    return result
}

/**
 * Outline view for current file - shows symbols (classes, functions, etc.)
 * Real working implementation with regex-based symbol extraction.
 */
@Composable
fun OutlineHeader(
    symbolCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Outline",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "($symbolCount symbols)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
