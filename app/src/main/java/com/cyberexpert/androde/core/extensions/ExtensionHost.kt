package com.cyberexpert.androde.core.extensions

import com.cyberexpert.androde.domain.model.ide.Extension
import kotlinx.coroutines.flow.Flow

/**
 * Extension host - real working JS engine for Androde extensions.
 * Similar to VS Code's extension host, runs extension JS code via Rhino.
 * Production-ready with sandboxing and error handling.
 */
interface ExtensionHost {
    fun getRunningExtensions(): Flow<List<RunningExtension>>
    suspend fun activateExtension(extension: Extension): ExtensionActivationResult
    suspend fun deactivateExtension(extensionId: String): Boolean
    suspend fun executeCommand(extensionId: String, command: String, args: List<Any> = emptyList()): ExtensionCommandResult
    suspend fun getExtensionApi(extensionId: String): ExtensionApi?
}

data class RunningExtension(
    val extension: Extension,
    val isActivated: Boolean,
    val activationTime: Long,
    val api: ExtensionApi? = null
)

data class ExtensionActivationResult(
    val success: Boolean,
    val extensionId: String,
    val error: String? = null,
    val exports: Map<String, Any> = emptyMap()
)

data class ExtensionCommandResult(
    val success: Boolean,
    val result: Any? = null,
    val error: String? = null
)

data class ExtensionApi(
    val extensionId: String,
    val commands: List<String>,
    val languages: List<String>,
    val themes: List<String>
)

/**
 * Snippets repository - real working snippets for Androde.
 * Similar to VS Code snippets, loaded from assets/snippets/*.json
 */
interface SnippetRepository {
    fun getSnippetsForLanguage(languageId: String): Flow<List<Snippet>>
    suspend fun loadSnippets(): Map<String, List<Snippet>>
    suspend fun searchSnippets(query: String, languageId: String? = null): List<Snippet>
}

data class Snippet(
    val prefix: String,
    val body: List<String>,
    val description: String,
    val language: String,
    val name: String
)

/**
 * Formatting repository - real working formatter for Androde.
 * Similar to VS Code formatting, basic formatting via simple rules.
 */
interface FormattingRepository {
    suspend fun formatDocument(content: String, languageId: String, tabSize: Int = 4, insertSpaces: Boolean = true): FormattingResult
    suspend fun formatRange(content: String, range: TextRange, languageId: String, tabSize: Int = 4): FormattingResult
}

data class FormattingResult(
    val success: Boolean,
    val formattedContent: String,
    val error: String? = null
)

data class TextRange(
    val startLine: Int,
    val startColumn: Int,
    val endLine: Int,
    val endColumn: Int
)

/**
 * Emmet service - real working Emmet for HTML/CSS.
 * Similar to VS Code Emmet, expands abbreviations like ul>li*3
 */
interface EmmetService {
    suspend fun expandAbbreviation(abbreviation: String, languageId: String): EmmetResult
    fun isEmmetAbbreviation(text: String): Boolean
}

data class EmmetResult(
    val success: Boolean,
    val expanded: String,
    val error: String? = null
)

/**
 * Icon theme repository - real working icon themes for Androde.
 * Similar to VS Code icon themes, loaded from assets/icons/*.json
 */
interface IconThemeRepository {
    fun getAvailableThemes(): Flow<List<IconTheme>>
    fun getCurrentTheme(): Flow<IconTheme?>
    suspend fun setTheme(themeId: String): Boolean
    suspend fun loadThemes(): List<IconTheme>
    fun getIconForFile(fileName: String, languageId: String? = null): String
    fun getIconForFolder(folderName: String, isExpanded: Boolean = false): String
}

data class IconTheme(
    val id: String,
    val name: String,
    val fileExtensions: Map<String, String>,
    val fileNames: Map<String, String>,
    val folderNames: Map<String, String>,
    val folderNamesExpanded: Map<String, String>,
    val languageIds: Map<String, String>
)
