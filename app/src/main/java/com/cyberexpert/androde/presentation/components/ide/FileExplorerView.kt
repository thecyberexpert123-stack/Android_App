package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.core.extensions.IconTheme
import com.cyberexpert.androde.domain.model.ide.FileNode
import java.io.File

/**
 * File Explorer view, similar to VS Code Explorer - Phase 5 with Icon Themes.
 * Features:
 * - Tree view with expand/collapse
 * - File icons by extension using IconTheme (vscode_icons, material_icons) with FileIconResolver
 * - Real icon mapping for 32 languages (kotlin, java, js, ts, python, html, css, json, md, yaml, xml, sql, csharp, dart, php, ruby, swift, properties, dockerfile, ini, toml, groovy, lua, r, bat, powershell, makefile, cmake, etc.)
 * - Folder icons with special handling for src, app, java, res, gradle, build, .git, test, etc.
 * - Git status colors (future)
 * - Context actions (rename, delete, new file/folder)
 * - Optimized for Android touch
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FileExplorerView(
    files: List<FileNode>,
    currentProjectPath: String?,
    onFileClick: (File) -> Unit,
    onFolderClick: (File) -> Unit,
    onFileLongClick: (File) -> Unit,
    modifier: Modifier = Modifier,
    iconTheme: IconTheme? = null
) {
    LazyColumn(modifier = modifier) {
        items(files, key = { it.path }) { node ->
            FileNodeItem(
                node = node,
                onFileClick = onFileClick,
                onFolderClick = onFolderClick,
                onLongClick = onFileLongClick,
                iconTheme = iconTheme
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FileNodeItem(
    node: FileNode,
    onFileClick: (File) -> Unit,
    onFolderClick: (File) -> Unit,
    onLongClick: (File) -> Unit,
    iconTheme: IconTheme? = null,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(node.isExpanded) }

    // Resolve icon ID using icon theme or fallback
    val iconId = FileIconResolver.resolveFileIcon(
        fileName = node.name,
        isDirectory = node.isDirectory,
        isExpanded = isExpanded,
        iconTheme = iconTheme
    )

    val (iconVector, iconTint) = if (node.isDirectory) {
        FileIconResolver.getFolderIconAndColor(iconId, isExpanded)
    } else {
        FileIconResolver.getIconAndColor(iconId)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (node.isDirectory) {
                        isExpanded = !isExpanded
                        onFolderClick(node.file)
                    } else {
                        onFileClick(node.file)
                    }
                },
                onLongClick = { onLongClick(node.file) }
            )
            .padding(start = (node.depth * 16).dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (node.isDirectory) {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = iconTint
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = node.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }

    // Render children if expanded
    if (node.isDirectory && isExpanded && node.children.isNotEmpty()) {
        Column {
            node.children.forEach { child ->
                FileNodeItem(
                    node = child,
                    onFileClick = onFileClick,
                    onFolderClick = onFolderClick,
                    onLongClick = onLongClick,
                    iconTheme = iconTheme
                )
            }
        }
    }
}
