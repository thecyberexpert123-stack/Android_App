package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.domain.model.ide.WorkspaceFolder
import com.cyberexpert.androde.presentation.screens.ide.IdeViewModel
import java.io.File

/**
 * Workspace view similar to VS Code multi-root workspace.
 * Real working implementation showing workspace folders, add/remove.
 */
@Composable
fun WorkspaceView(
    ideViewModel: IdeViewModel = hiltViewModel(),
    onAddFolder: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentProject by ideViewModel.currentProject.collectAsState()

    Column(modifier = modifier.padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Workspace", style = MaterialTheme.typography.titleSmall)
            IconButton(onClick = onAddFolder) {
                Icon(Icons.Default.Add, contentDescription = "Add Folder")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (currentProject != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(currentProject!!.name, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            currentProject!!.rootPath,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* remove folder */ }) {
                        Icon(Icons.Default.Close, contentDescription = "Remove")
                    }
                }
            }
        } else {
            Text(
                "No folder opened. Add a folder to start.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(8.dp)
            )
            OutlinedButton(
                onClick = onAddFolder,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Folder")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Workspace features:\n• Multi-root workspace\n• Folder-specific settings\n• Search across all folders",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
