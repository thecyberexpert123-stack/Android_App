package com.cyberexpert.androde.presentation.screens.emmet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

/**
 * Emmet screen - real working VS Code like Emmet.
 * Expands abbreviations like ul>li*3, div#id.class, etc.
 * Production-ready with real EmmetService.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmmetScreen(
    viewModel: EmmetViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var abbreviation by remember { mutableStateOf("ul>li*3") }
    var expanded by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("html") }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Emmet - Expand Abbreviation") })
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Emmet examples: ul>li*3, div#header.container, a[href=#]{Click}, (div>ul)+p, m10, p20",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("html", "css").forEach { lang ->
                    androidx.compose.material3.FilterChip(
                        selected = language == lang,
                        onClick = { language = lang },
                        label = { Text(lang) }
                    )
                }
            }

            OutlinedTextField(
                value = abbreviation,
                onValueChange = { abbreviation = it },
                label = { Text("Emmet Abbreviation") },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                )
            )

            Button(
                onClick = {
                    scope.launch {
                        val result = viewModel.expandAbbreviation(abbreviation, language)
                        if (result.success) {
                            expanded = result.expanded
                            error = null
                        } else {
                            error = result.error
                            expanded = result.expanded
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Expand (Tab)")
            }

            error?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Expanded:", style = MaterialTheme.typography.titleSmall)
                    Text(
                        text = expanded.ifBlank { "Expanded code will appear here" },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
