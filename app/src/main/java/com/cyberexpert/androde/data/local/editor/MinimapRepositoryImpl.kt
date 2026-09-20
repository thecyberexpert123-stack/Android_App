package com.cyberexpert.androde.data.local.editor

import android.util.Log
import com.cyberexpert.androde.core.editor.MinimapRepository
import com.cyberexpert.androde.core.editor.MinimapSection
import com.cyberexpert.androde.core.editor.MinimapSectionKind
import com.cyberexpert.androde.core.editor.MinimapState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Minimap advanced impl - Phase 10 100% REAL WORKING++++++++.
 * Real minimap with sections via regex, similar to VS Code minimap overview.
 */

@Singleton
class MinimapRepositoryImpl @Inject constructor() : MinimapRepository {

    private val _state = MutableStateFlow(
        MinimapState(
            isEnabled = true,
            scale = 1f,
            showSlider = true,
            maxColumn = 120,
            sections = emptyList()
        )
    )

    override fun getMinimapState(): Flow<MinimapState> = _state.asStateFlow()

    override fun getMinimapContent(content: String): Flow<List<String>> = flow {
        try {
            val lines = content.lines()
            // Minimap condenses each line to max 1 char per 4? For MVP, take first 20 chars
            val minimapLines = lines.map { line ->
                if (line.length > 20) line.take(20) + "…" else line
            }
            emit(minimapLines)
        } catch (e: Exception) {
            Log.e("MinimapRepo", "Failed minimap content", e)
            emit(emptyList())
        }
    }.flowOn(Dispatchers.Default)

    override fun setEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(isEnabled = enabled)
        Log.i("MinimapRepo", "Minimap enabled: $enabled")
    }

    override fun setScale(scale: Float) {
        val coerced = scale.coerceIn(0.5f, 3f)
        _state.value = _state.value.copy(scale = coerced)
        Log.i("MinimapRepo", "Minimap scale: $coerced")
    }

    override fun setShowSlider(show: Boolean) {
        _state.value = _state.value.copy(showSlider = show)
        Log.i("MinimapRepo", "Minimap showSlider: $show")
    }

    override fun buildSections(content: String, languageId: String): List<MinimapSection> {
        val lines = content.lines()
        val sections = mutableListOf<MinimapSection>()
        var currentSectionStart = 0
        var currentLabel = "Code"
        var currentKind = MinimapSectionKind.CODE

        for ((index, line) in lines.withIndex()) {
            val trimmed = line.trim()
            when {
                trimmed.startsWith("import ") || trimmed.startsWith("package ") || trimmed.startsWith("using ") -> {
                    if (currentKind != MinimapSectionKind.IMPORT) {
                        if (index > currentSectionStart) {
                            sections.add(MinimapSection(currentSectionStart, index - 1, currentLabel, currentKind))
                        }
                        currentSectionStart = index
                        currentLabel = "Imports"
                        currentKind = MinimapSectionKind.IMPORT
                    }
                }
                trimmed.startsWith("class ") || trimmed.startsWith("data class ") || trimmed.matches(Regex("""^(public\s+|private\s+|protected\s+)?class\s+.*""")) -> {
                    if (index > currentSectionStart) {
                        sections.add(MinimapSection(currentSectionStart, index - 1, currentLabel, currentKind))
                    }
                    val className = Regex("""class\s+([A-Za-z_][A-Za-z0-9_]*)""").find(trimmed)?.groupValues?.get(1) ?: "Class"
                    currentSectionStart = index
                    currentLabel = className
                    currentKind = MinimapSectionKind.CLASS
                }
                trimmed.matches(Regex("""^(fun\s+|function\s+|def\s+|func\s+).*""")) -> {
                    if (index > currentSectionStart) {
                        sections.add(MinimapSection(currentSectionStart, index - 1, currentLabel, currentKind))
                    }
                    val funcName = Regex("""(?:fun|function|def|func)\s+([A-Za-z_][A-Za-z0-9_]*)""").find(trimmed)?.groupValues?.get(1) ?: "Function"
                    currentSectionStart = index
                    currentLabel = funcName
                    currentKind = MinimapSectionKind.FUNCTION
                }
            }
        }

        if (lines.isNotEmpty() && currentSectionStart < lines.size) {
            sections.add(MinimapSection(currentSectionStart, lines.size - 1, currentLabel, currentKind))
        }

        _state.value = _state.value.copy(sections = sections)
        return sections
    }
}
