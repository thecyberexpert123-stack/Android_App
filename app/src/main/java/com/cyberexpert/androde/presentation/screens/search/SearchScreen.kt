package com.cyberexpert.androde.presentation.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.domain.model.ide.SearchResult
import java.io.File

/**
 * Search screen, similar to VS Code Search view (Ctrl+Shift+F).
 * Features:
 * - Search query with regex, case sensitive, whole word
 * - Results grouped by file
 * - Preview of matches
 */
@Composable
fun SearchScreen(
    rootFile: File,
    onResultClick: (SearchResult) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val totalMatches by viewModel.totalMatches.collectAsState()

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        OutlinedTextField(
            value = query.query,
            onValueChange = { viewModel.updateQuery(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = query.isRegex,
                onClick = { viewModel.toggleRegex() },
                label = { Text(".*") }
            )
            FilterChip(
                selected = query.isCaseSensitive,
                onClick = { viewModel.toggleCaseSensitive() },
                label = { Text("Aa") }
            )
            FilterChip(
                selected = query.isWholeWord,
                onClick = { viewModel.toggleWholeWord() },
                label = { Text("Ab|") }
            )
        }

        androidx.compose.material3.Button(
            onClick = { viewModel.search(rootFile) },
            modifier = Modifier.fillMaxWidth(),
            enabled = query.query.isNotBlank() && !isSearching
        ) {
            Text(if (isSearching) "Searching..." else "Search in ${rootFile.name}")
        }

        Text(
            text = if (isSearching) "Searching..." else "$totalMatches results in ${results.map { it.filePath }.distinct().size} files",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(results, key = { "${it.filePath}:${it.lineNumber}:${it.column}" }) { result ->
                SearchResultItem(result = result, onClick = { onResultClick(result) })
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "${result.fileName}:${result.lineNumber}",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = result.lineContent.trim(),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2
            )
        }
    }
}
