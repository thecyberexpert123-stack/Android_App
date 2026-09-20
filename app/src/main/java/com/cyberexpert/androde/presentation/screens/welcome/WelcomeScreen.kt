package com.cyberexpert.androde.presentation.screens.welcome

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.Project

/**
 * Welcome screen, similar to VS Code Welcome page.
 * Shows recent projects, open folder, new file, etc.
 */
@Composable
fun WelcomeScreen(
    recentProjects: List<Project>,
    onOpenFolder: () -> Unit,
    onOpenRecent: (Project) -> Unit,
    onNewFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.Code,
                    contentDescription = null,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = "Androde",
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = "VS Code for Android - Code. Anywhere.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = onOpenFolder) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open Folder")
                    }
                    OutlinedButton(onClick = onNewFile) {
                        Text("New File")
                    }
                }
            }
        }

        if (recentProjects.isNotEmpty()) {
            item {
                Text(
                    text = "Recent Projects",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(recentProjects) { project ->
                Card(
                    onClick = { onOpenRecent(project) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.FolderOpen, contentDescription = null, modifier = Modifier.padding(end = 12.dp))
                        Column {
                            Text(project.displayName, style = MaterialTheme.typography.bodyMedium)
                            Text(project.rootPath, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Learn",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth()
            )
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row {
                        Icon(Icons.Default.Newspaper, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
                        Text("Androde is VS Code for Android with Sora Editor, JGit, Terminal, Search, Extensions, Command Palette, and more.", style = MaterialTheme.typography.bodySmall)
                    }
                    Text("• Explorer with tree view, file icons, git status", style = MaterialTheme.typography.bodySmall)
                    Text("• Search with regex, case, whole word", style = MaterialTheme.typography.bodySmall)
                    Text("• Source Control with JGit", style = MaterialTheme.typography.bodySmall)
                    Text("• Integrated Terminal with multiple sessions", style = MaterialTheme.typography.bodySmall)
                    Text("• Command Palette with 20+ commands", style = MaterialTheme.typography.bodySmall)
                    Text("• Settings with themes (VS Code Dark+, Monokai, Dracula)", style = MaterialTheme.typography.bodySmall)
                    Text("• Extensions marketplace", style = MaterialTheme.typography.bodySmall)
                    Text("• Split Editor, Problems, Output, Debug (architecture ready)", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
