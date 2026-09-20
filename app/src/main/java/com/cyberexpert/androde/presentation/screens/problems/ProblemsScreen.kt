package com.cyberexpert.androde.presentation.screens.problems

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.domain.model.ide.Diagnostic
import com.cyberexpert.androde.domain.model.ide.DiagnosticSeverity

/**
 * Problems panel, similar to VS Code Problems view (Ctrl+Shift+M).
 * Shows diagnostics from LSP, linters, compilers.
 */
@Composable
fun ProblemsScreen(
    viewModel: ProblemsViewModel = hiltViewModel(),
    onDiagnosticClick: (Diagnostic) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val diagnostics by viewModel.diagnostics.collectAsState()
    var filter by remember { mutableStateOf<DiagnosticSeverity?>(null) }

    val filtered = if (filter == null) diagnostics else diagnostics.filter { it.severity == filter }

    Column(modifier = modifier.fillMaxSize().padding(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filter == null,
                onClick = { filter = null },
                label = { Text("All (${diagnostics.size})") }
            )
            FilterChip(
                selected = filter == DiagnosticSeverity.ERROR,
                onClick = { filter = if (filter == DiagnosticSeverity.ERROR) null else DiagnosticSeverity.ERROR },
                label = { Text("Errors (${diagnostics.count { it.severity == DiagnosticSeverity.ERROR }})") }
            )
            FilterChip(
                selected = filter == DiagnosticSeverity.WARNING,
                onClick = { filter = if (filter == DiagnosticSeverity.WARNING) null else DiagnosticSeverity.WARNING },
                label = { Text("Warnings (${diagnostics.count { it.severity == DiagnosticSeverity.WARNING }})") }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(filtered, key = { it.id }) { diagnostic ->
                DiagnosticItem(diagnostic = diagnostic, onClick = { onDiagnosticClick(diagnostic) })
            }
        }
    }
}

@Composable
private fun DiagnosticItem(
    diagnostic: Diagnostic,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            Icon(
                imageVector = when (diagnostic.severity) {
                    DiagnosticSeverity.ERROR -> Icons.Default.Error
                    DiagnosticSeverity.WARNING -> Icons.Default.Warning
                    else -> Icons.Default.Info
                },
                contentDescription = null,
                tint = when (diagnostic.severity) {
                    DiagnosticSeverity.ERROR -> Color.Red
                    DiagnosticSeverity.WARNING -> Color(0xFFCCA700)
                    DiagnosticSeverity.INFO -> Color.Blue
                    DiagnosticSeverity.HINT -> Color.Gray
                },
                modifier = Modifier.padding(end = 8.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diagnostic.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${diagnostic.fileName}:${diagnostic.line}:${diagnostic.column} - ${diagnostic.source}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
