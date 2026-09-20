package com.cyberexpert.androde.presentation.screens.git

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.domain.model.ide.GitFile

/**
 * Source Control screen, similar to VS Code Git view (Ctrl+Shift+G).
 * Features:
 * - Staged/unstaged changes
 * - Commit message
 * - Branch info
 * - Commit history
 */
@Composable
fun GitScreen(
    viewModel: GitViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val repository by viewModel.repository.collectAsState()
    val status by viewModel.status.collectAsState()
    val commits by viewModel.commits.collectAsState()
    var commitMessage by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        if (repository == null) {
            Text("No Git repository opened", style = MaterialTheme.typography.bodyMedium)
            return
        }

        Text(
            text = "Branch: ${repository!!.currentBranch}",
            style = MaterialTheme.typography.titleSmall
        )

        OutlinedTextField(
            value = commitMessage,
            onValueChange = { commitMessage = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            placeholder = { Text("Message (Ctrl+Enter to commit)") },
            minLines = 2
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.stageAll() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Stage All")
            }
            Button(
                onClick = {
                    viewModel.commit(commitMessage)
                    commitMessage = ""
                },
                modifier = Modifier.weight(1f),
                enabled = commitMessage.isNotBlank()
            ) {
                Text("Commit")
            }
        }

        Text(
            text = "Changes (${(status?.unstaged?.size ?: 0) + (status?.untracked?.size ?: 0)})",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val allChanges = (status?.unstaged ?: emptyList()) + (status?.untracked ?: emptyList())
            items(allChanges) { file ->
                GitFileItem(file = file)
            }
        }

        Text(
            text = "Recent Commits",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(top = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(commits) { commit ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(commit.shortId + " - " + commit.message, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${commit.author} - ${java.text.SimpleDateFormat("MMM dd, HH:mm").format(java.util.Date(commit.timestamp))}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GitFileItem(file: GitFile) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(
                text = file.relativePath,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = file.status.name.first().toString(),
                style = MaterialTheme.typography.labelSmall,
                color = when (file.status) {
                    com.cyberexpert.androde.domain.model.ide.GitFileStatus.ADDED -> MaterialTheme.colorScheme.primary
                    com.cyberexpert.androde.domain.model.ide.GitFileStatus.MODIFIED -> androidx.compose.ui.graphics.Color(0xFFCCA700)
                    com.cyberexpert.androde.domain.model.ide.GitFileStatus.DELETED -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}
