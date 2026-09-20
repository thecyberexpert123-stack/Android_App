package com.cyberexpert.androde.core.editor

import kotlinx.coroutines.flow.Flow

/**
 * CodeLens, Inlay Hints, Semantic Tokens - Phase 8, similar to VS Code advanced editor features.
 * Production-ready with real models and flows.
 */

data class CodeLens(
    val id: String,
    val filePath: String,
    val line: Int,
    val command: String,
    val title: String,
    val tooltip: String? = null,
    val range: com.cyberexpert.androde.domain.model.ide.TextRange? = null
)

data class InlayHint(
    val id: String,
    val filePath: String,
    val line: Int,
    val column: Int,
    val label: String,
    val kind: InlayHintKind = InlayHintKind.PARAMETER,
    val tooltip: String? = null
)

enum class InlayHintKind { PARAMETER, TYPE, OTHER }

data class SemanticToken(
    val line: Int,
    val column: Int,
    val length: Int,
    val tokenType: String, // class, function, variable, etc.
    val tokenModifiers: List<String> = emptyList()
)

data class SemanticTokens(
    val filePath: String,
    val tokens: List<SemanticToken> = emptyList()
)

interface CodeLensRepository {
    fun getCodeLenses(filePath: String): Flow<List<CodeLens>>
    fun getInlayHints(filePath: String): Flow<List<InlayHint>>
    fun getSemanticTokens(filePath: String): Flow<SemanticTokens>
    suspend fun provideCodeLenses(filePath: String, content: String): List<CodeLens>
    suspend fun provideInlayHints(filePath: String, content: String): List<InlayHint>
    suspend fun provideSemanticTokens(filePath: String, content: String): SemanticTokens
}
