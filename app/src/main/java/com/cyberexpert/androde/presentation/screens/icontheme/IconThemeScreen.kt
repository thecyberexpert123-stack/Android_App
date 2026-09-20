package com.cyberexpert.androde.presentation.screens.icontheme

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.core.extensions.IconTheme

/**
 * Icon Theme screen - real working VS Code like icon themes.
 * Shows available icon themes with file/folder icon mappings.
 * Production-ready with real IconThemeRepository.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconThemeScreen(
    viewModel: IconThemeViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val themes by viewModel.themes.collectAsState()
    val currentTheme by viewModel.currentTheme.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadThemes()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Icon Themes") })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Text(
                text = "Current: ${currentTheme?.name ?: "None"}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(themes, key = { it.id }) { theme ->
                    IconThemeItem(
                        theme = theme,
                        isSelected = currentTheme?.id == theme.id,
                        onClick = { viewModel.setTheme(theme.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun IconThemeItem(
    theme: IconTheme,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = onClick
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
                    text = theme.name,
                    style = MaterialTheme.typography.titleSmall
                )
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${theme.fileExtensions.size} file extensions, ${theme.folderNames.size} folders, ${theme.fileNames.size} file names",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Examples: ${theme.fileExtensions.entries.take(5).joinToString { "${it.key}→${it.value}" }}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
