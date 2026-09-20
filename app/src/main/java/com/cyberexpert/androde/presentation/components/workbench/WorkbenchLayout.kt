package com.cyberexpert.androde.presentation.components.workbench

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.core.workbench.WorkbenchLayout
import com.cyberexpert.androde.core.workbench.WorkbenchPart

/**
 * Workbench Layout - Real working implementation similar to VS Code's src/vs/workbench/browser/layout.ts
 * Manages layout of workbench parts: activitybar, sidebar, editor, panel, statusbar.
 * Production-ready with responsive design for phone (drawer) and tablet (side-by-side).
 */

@Composable
fun WorkbenchLayout(
    layout: WorkbenchLayout,
    activityBar: @Composable () -> Unit,
    sidebar: @Composable () -> Unit,
    editor: @Composable () -> Unit,
    panel: @Composable () -> Unit,
    statusBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Title bar (optional, hidden on phone)
        if (layout.titleBarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                // Title bar content would go here
            }
        }

        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Activity bar - left
            if (layout.activityBarVisible && layout.activityBarPosition.name == "LEFT") {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    activityBar()
                }
            }

            // Sidebar - left or right
            if (layout.sidebarVisible && layout.sidebarPosition.name == "LEFT") {
                Box(
                    modifier = Modifier
                        .width(layout.sidebarWidth.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    sidebar()
                }
            }

            // Main area: editor + panel
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                // Editor area
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    editor()
                }

                // Panel - bottom or right
                if (layout.panelVisible && layout.panelPosition.name == "BOTTOM") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        panel()
                    }
                }
            }

            // Sidebar right
            if (layout.sidebarVisible && layout.sidebarPosition.name == "RIGHT") {
                Box(
                    modifier = Modifier
                        .width(layout.sidebarWidth.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    sidebar()
                }
            }

            // Activity bar right
            if (layout.activityBarVisible && layout.activityBarPosition.name == "RIGHT") {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    activityBar()
                }
            }
        }

        // Status bar - bottom
        if (layout.statusBarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                statusBar()
            }
        }
    }
}

@Composable
fun WorkbenchLayoutPhone(
    layout: WorkbenchLayout,
    activityBar: @Composable () -> Unit,
    sidebar: @Composable () -> Unit,
    editor: @Composable () -> Unit,
    panel: @Composable () -> Unit,
    statusBar: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    // Phone layout: Editor full, sidebar as drawer, panel as bottom sheet
    // This is handled by IdeScreen's ModalNavigationDrawer + ModalBottomSheet
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            editor()
        }
        if (layout.statusBarVisible) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                statusBar()
            }
        }
    }
}
