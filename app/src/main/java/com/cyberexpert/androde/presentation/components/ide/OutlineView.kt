package com.cyberexpert.androde.presentation.components.ide

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cyberexpert.androde.domain.model.ide.EditorTab

/**
 * Outline view similar to VS Code Outline.
 * Real working implementation that parses current file content for symbols.
 * Shows classes, functions, variables, etc. with icons and line numbers.
 */
@Composable
fun OutlineView(
    tab: EditorTab?,
    onSymbolClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tab == null) {
        Text("No file open", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
        return
    }

    val symbols = remember(tab.content, tab.language) {
        extractSymbols(tab.content, tab.language.id)
    }

    Column(modifier = modifier) {
        OutlineHeader(symbolCount = symbols.size)

        LazyColumn {
            items(symbols, key = { "${it.name}-${it.line}" }) { symbol ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSymbolClick(symbol.line) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (symbol.kind) {
                            SymbolKind.CLASS -> Icons.Default.DataObject
                            SymbolKind.FUNCTION -> Icons.Default.Functions
                            else -> Icons.Default.Code
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = when (symbol.kind) {
                            SymbolKind.CLASS -> MaterialTheme.colorScheme.primary
                            SymbolKind.FUNCTION -> MaterialTheme.colorScheme.secondary
                            SymbolKind.VARIABLE -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = symbol.name,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = ":${symbol.line + 1}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

data class Symbol(
    val name: String,
    val kind: SymbolKind,
    val line: Int,
    val detail: String = ""
)

enum class SymbolKind {
    CLASS, FUNCTION, VARIABLE, CONSTANT, INTERFACE, ENUM, PROPERTY
}

/**
 * Real symbol extraction using regex - similar to VS Code's outline but lightweight.
 * Production would use TreeSitter, but regex works for 100% real working MVP.
 */
fun extractSymbols(content: String, languageId: String): List<Symbol> {
    val symbols = mutableListOf<Symbol>()
    val lines = content.lines()

    val patterns = when (languageId) {
        "kotlin" -> listOf(
            Regex("""^\s*(?:data\s+)?(?:sealed\s+)?(?:open\s+)?(?:abstract\s+)?class\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.CLASS,
            Regex("""^\s*(?:object|interface|enum\s+class|enum)\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.CLASS,
            Regex("""^\s*(?:fun|private\s+fun|public\s+fun|internal\s+fun|protected\s+fun|override\s+fun)\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.FUNCTION,
            Regex("""^\s*(?:val|var)\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.VARIABLE
        )
        "java" -> listOf(
            Regex("""^\s*(?:public\s+|private\s+|protected\s+)?(?:abstract\s+|final\s+)?class\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.CLASS,
            Regex("""^\s*(?:public\s+|private\s+|protected\s+)?interface\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.INTERFACE,
            Regex("""^\s*(?:public\s+|private\s+|protected\s+)?(?:static\s+)?[A-Za-z0-9_<>\[\]]+\s+([A-Za-z_][A-Za-z0-9_]*)\s*\(""") to SymbolKind.FUNCTION
        )
        "javascript", "typescript" -> listOf(
            Regex("""^\s*(?:export\s+)?class\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.CLASS,
            Regex("""^\s*(?:export\s+)?(?:async\s+)?function\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.FUNCTION,
            Regex("""^\s*(?:const|let|var)\s+([A-Za-z_][A-Za-z0-9_]*)\s*=\s*(?:\(.*\)\s*=>|function)""") to SymbolKind.FUNCTION,
            Regex("""^\s*(?:const|let|var)\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.VARIABLE
        )
        "python" -> listOf(
            Regex("""^\s*class\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.CLASS,
            Regex("""^\s*def\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.FUNCTION
        )
        else -> listOf(
            Regex("""^\s*(?:class|function|def|func)\s+([A-Za-z_][A-Za-z0-9_]*)""") to SymbolKind.FUNCTION
        )
    }

    lines.forEachIndexed { index, line ->
        for ((regex, kind) in patterns) {
            val match = regex.find(line)
            if (match != null && match.groupValues.size > 1) {
                val name = match.groupValues[1]
                if (name.isNotBlank() && name.length > 1) {
                    symbols.add(Symbol(name, kind, index, line.trim()))
                }
                break
            }
        }
    }

    return symbols.distinctBy { it.name to it.line }.take(200)
}
