package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.EditorGroup
import com.cyberexpert.androde.domain.model.ide.EditorTab
import com.cyberexpert.androde.domain.model.ide.SplitDirection

/**
 * Split editor view, similar to VS Code split editor (Ctrl+\).
 * Supports horizontal and vertical splits, multiple editor groups.
 */
@Composable
fun SplitEditorView(
    groups: List<EditorGroup>,
    direction: SplitDirection,
    onTabSelected: (groupId: String, tabId: String) -> Unit,
    onTabClose: (groupId: String, tabId: String) -> Unit,
    onContentChange: (groupId: String, tabId: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (groups.isEmpty()) {
        Box(modifier = modifier.fillMaxSize()) {
            Text("No editor groups", modifier = Modifier.padding(16.dp))
        }
        return
    }

    when (direction) {
        SplitDirection.HORIZONTAL, SplitDirection.LEFT, SplitDirection.RIGHT -> {
            Row(modifier = modifier.fillMaxSize()) {
                groups.forEach { group ->
                    EditorGroupView(
                        group = group,
                        onTabSelected = { tabId -> onTabSelected(group.id, tabId) },
                        onTabClose = { tabId -> onTabClose(group.id, tabId) },
                        onContentChange = { tabId, content -> onContentChange(group.id, tabId, content) },
                        modifier = Modifier.weight(1f).fillMaxSize()
                    )
                }
            }
        }
        SplitDirection.VERTICAL, SplitDirection.UP, SplitDirection.DOWN -> {
            Column(modifier = modifier.fillMaxSize()) {
                groups.forEach { group ->
                    EditorGroupView(
                        group = group,
                        onTabSelected = { tabId -> onTabSelected(group.id, tabId) },
                        onTabClose = { tabId -> onTabClose(group.id, tabId) },
                        onContentChange = { tabId, content -> onContentChange(group.id, tabId, content) },
                        modifier = Modifier.weight(1f).fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun EditorGroupView(
    group: EditorGroup,
    onTabSelected: (tabId: String) -> Unit,
    onTabClose: (tabId: String) -> Unit,
    onContentChange: (tabId: String, content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (group.tabs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("No open editors", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
            }
        } else {
            // Tabs bar for this group
            androidx.compose.foundation.lazy.LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(group.tabs.size) { index ->
                    val tab = group.tabs[index]
                    androidx.compose.material3.AssistChip(
                        onClick = { onTabSelected(tab.id) },
                        label = { Text(tab.fileName + if (tab.isDirty) " •" else "") },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
            // Active editor
            val activeTab = group.activeTab
            if (activeTab != null) {
                SoraEditorView(
                    tab = activeTab,
                    onContentChange = { content -> onContentChange(activeTab.id, content) },
                    modifier = Modifier.weight(1f).fillMaxSize()
                )
            }
        }
    }
}
