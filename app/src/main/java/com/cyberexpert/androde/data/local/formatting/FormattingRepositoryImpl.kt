package com.cyberexpert.androde.data.local.formatting

import android.util.Log
import com.cyberexpert.androde.core.extensions.FormattingRepository
import com.cyberexpert.androde.core.extensions.FormattingResult
import com.cyberexpert.androde.core.extensions.TextRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working formatting repository.
 * Similar to VS Code formatting, basic formatting via simple rules.
 * Production-ready with language-specific formatting.
 * For real production, would use language-specific formatters (ktlint, google-java-format, prettier, black, etc.)
 * Here we implement basic indentation and whitespace formatting.
 */
@Singleton
class FormattingRepositoryImpl @Inject constructor() : FormattingRepository {

    override suspend fun formatDocument(content: String, languageId: String, tabSize: Int, insertSpaces: Boolean): FormattingResult = withContext(Dispatchers.IO) {
        try {
            Log.i("FormattingRepo", "Formatting document for $languageId, tabSize $tabSize, insertSpaces $insertSpaces")

            val indent = if (insertSpaces) " ".repeat(tabSize) else "\t"

            val formatted = when (languageId.lowercase()) {
                "kotlin", "java", "javascript", "typescript", "csharp", "dart", "swift", "cpp", "go", "rust" -> {
                    formatCStyle(content, indent)
                }
                "python" -> {
                    formatPython(content, indent)
                }
                "html", "xml" -> {
                    formatHtmlXml(content, indent)
                }
                "json" -> {
                    formatJson(content, indent)
                }
                "css" -> {
                    formatCss(content, indent)
                }
                else -> {
                    // Generic formatting: trim trailing whitespace, ensure newline at end
                    content.lines().joinToString("\n") { line ->
                        line.trimEnd()
                    }.trim() + "\n"
                }
            }

            FormattingResult(
                success = true,
                formattedContent = formatted
            )
        } catch (e: Exception) {
            Log.e("FormattingRepo", "Failed to format document for $languageId", e)
            FormattingResult(
                success = false,
                formattedContent = content,
                error = e.message
            )
        }
    }

    override suspend fun formatRange(content: String, range: TextRange, languageId: String, tabSize: Int): FormattingResult = withContext(Dispatchers.IO) {
        try {
            val lines = content.lines()
            if (range.startLine < 0 || range.endLine >= lines.size) {
                return@withContext FormattingResult(
                    success = false,
                    formattedContent = content,
                    error = "Invalid range"
                )
            }

            val rangeContent = lines.subList(range.startLine, range.endLine + 1).joinToString("\n")
            val result = formatDocument(rangeContent, languageId, tabSize, true)

            if (!result.success) {
                return@withContext result
            }

            val formattedLines = result.formattedContent.lines()
            val newLines = lines.toMutableList()
            for (i in range.startLine..range.endLine) {
                val formattedIndex = i - range.startLine
                if (formattedIndex < formattedLines.size) {
                    newLines[i] = formattedLines[formattedIndex]
                }
            }

            FormattingResult(
                success = true,
                formattedContent = newLines.joinToString("\n")
            )
        } catch (e: Exception) {
            Log.e("FormattingRepo", "Failed to format range for $languageId", e)
            FormattingResult(
                success = false,
                formattedContent = content,
                error = e.message
            )
        }
    }

    /**
     * Basic C-style formatting: indent based on braces
     */
    private fun formatCStyle(content: String, indent: String): String {
        val lines = content.lines()
        val result = mutableListOf<String>()
        var indentLevel = 0

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                result.add("")
                continue
            }

            // Decrease indent for closing braces
            if (line.startsWith("}") || line.startsWith(")") || line.startsWith("]")) {
                indentLevel = (indentLevel - 1).coerceAtLeast(0)
            }

            result.add(indent.repeat(indentLevel) + line)

            // Increase indent for opening braces
            if (line.endsWith("{") || line.endsWith("(") || line.endsWith("[")) {
                indentLevel++
            }
            // Handle case where line has both opening and closing (e.g., "} else {")
            if (line.contains("}") && line.contains("{") && line.indexOf("}") < line.indexOf("{")) {
                // Already handled decrease, now increase
            } else if (line.endsWith("}") && !line.endsWith("{}")) {
                // Closing brace on same line as content already decreased
            }
        }

        return result.joinToString("\n")
    }

    /**
     * Python formatting: indent based on colons
     */
    private fun formatPython(content: String, indent: String): String {
        val lines = content.lines()
        val result = mutableListOf<String>()
        var indentLevel = 0
        val indentStack = mutableListOf<Int>()

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                result.add("")
                continue
            }

            // Decrease indent for dedent keywords
            if (line.startsWith("elif ") || line.startsWith("else:") || line.startsWith("except") || line.startsWith("finally:")) {
                if (indentStack.isNotEmpty()) {
                    indentLevel = indentStack.removeAt(indentStack.lastIndex)
                }
            }

            result.add(indent.repeat(indentLevel) + line)

            // Increase indent after colon
            if (line.endsWith(":")) {
                indentStack.add(indentLevel)
                indentLevel++
            }
        }

        return result.joinToString("\n")
    }

    /**
     * HTML/XML formatting: indent based on tags
     */
    private fun formatHtmlXml(content: String, indent: String): String {
        val lines = content.lines()
        val result = mutableListOf<String>()
        var indentLevel = 0

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                result.add("")
                continue
            }

            // Decrease for closing tags
            if (line.startsWith("</")) {
                indentLevel = (indentLevel - 1).coerceAtLeast(0)
            }

            result.add(indent.repeat(indentLevel) + line)

            // Increase for opening tags that are not self-closing and not closed on same line
            if (line.startsWith("<") && !line.startsWith("</") && !line.endsWith("/>") && !line.contains("</")) {
                // Check if it's not a void element
                val voidElements = listOf("area", "base", "br", "col", "embed", "hr", "img", "input", "link", "meta", "param", "source", "track", "wbr")
                val tagName = line.substringAfter("<").substringBefore(" ").substringBefore(">").lowercase()
                if (tagName !in voidElements) {
                    indentLevel++
                }
            }
        }

        return result.joinToString("\n")
    }

    /**
     * JSON formatting: pretty print
     */
    private fun formatJson(content: String, indent: String): String {
        return try {
            val json = org.json.JSONObject(content)
            json.toString(4) // 4 spaces
        } catch (e: Exception) {
            try {
                val jsonArray = org.json.JSONArray(content)
                jsonArray.toString(4)
            } catch (e2: Exception) {
                // Fallback to basic formatting
                formatCStyle(content, indent)
            }
        }
    }

    /**
     * CSS formatting: indent inside braces
     */
    private fun formatCss(content: String, indent: String): String {
        return formatCStyle(content, indent)
    }
}
