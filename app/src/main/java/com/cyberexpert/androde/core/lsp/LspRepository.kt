package com.cyberexpert.androde.core.lsp

import com.cyberexpert.androde.domain.model.ide.Diagnostic
import kotlinx.coroutines.flow.Flow

/**
 * LSP (Language Server Protocol) repository, similar to VS Code's language client.
 * Provides IntelliSense: completion, hover, go to definition, diagnostics.
 * Uses LSP4J for protocol handling.
 */
interface LspRepository {
    fun getDiagnostics(): Flow<List<Diagnostic>>
    suspend fun startServer(languageId: String, rootPath: String): Boolean
    suspend fun stopServer(languageId: String)
    suspend fun didOpen(filePath: String, content: String, languageId: String)
    suspend fun didChange(filePath: String, content: String)
    suspend fun didClose(filePath: String)
    suspend fun completion(filePath: String, line: Int, column: Int): List<CompletionItem>
    suspend fun hover(filePath: String, line: Int, column: Int): Hover?
    suspend fun definition(filePath: String, line: Int, column: Int): List<Location>
}

data class CompletionItem(
    val label: String,
    val kind: String,
    val detail: String? = null,
    val insertText: String = label
)

data class Hover(
    val contents: String,
    val range: Range? = null
)

data class Location(
    val filePath: String,
    val range: Range
)

data class Range(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)
