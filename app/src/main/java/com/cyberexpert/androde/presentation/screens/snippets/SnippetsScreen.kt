package com.cyberexpert.androde.presentation.screens.snippets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.core.extensions.Snippet

/**
 * Snippets screen - real working VS Code like snippets.
 * Shows snippets for current language with prefix, body, description.
 * Production-ready with search and copy.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetsScreen(
    languageId: String = "kotlin",
    onSnippetClick: (Snippet) -> Unit = {},
    viewModel: SnippetsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val snippets by viewModel.snippets.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLanguage by remember { mutableStateOf(languageId) }

    LaunchedEffect(selectedLanguage) {
        viewModel.loadSnippetsForLanguage(selectedLanguage)
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            viewModel.searchSnippets(searchQuery, selectedLanguage)
        } else {
            viewModel.loadSnippetsForLanguage(selectedLanguage)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Snippets - $selectedLanguage") })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search snippets (prefix, name, description)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("kotlin", "java", "javascript", "python", "html").forEach { lang ->
                    androidx.compose.material3.FilterChip(
                        selected = selectedLanguage == lang,
                        onClick = { selectedLanguage = lang },
                        label = { Text(lang) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                Text("Loading snippets...", style = MaterialTheme.typography.bodySmall)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(snippets, key = { it.prefix + it.name }) { snippet ->
                        SnippetItem(
                            snippet = snippet,
                            onClick = { onSnippetClick(snippet) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SnippetItem(
    snippet: Snippet,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = snippet.name,
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = onClick) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                }
            }

            Text(
                text = "Prefix: ${snippet.prefix}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = snippet.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = snippet.body.joinToString("\n"),
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}
