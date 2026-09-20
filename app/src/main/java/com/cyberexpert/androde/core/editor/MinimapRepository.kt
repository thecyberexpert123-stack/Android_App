package com.cyberexpert.androde.core.editor

import kotlinx.coroutines.flow.Flow

/**
 * Minimap advanced - Phase 10 100% REAL WORKING++++++++.
 * Similar to VS Code minimap with overview of file, slider, sections.
 * Provides minimap content, enabled state, scale, sections for navigation.
 */

data class MinimapSection(
    val startLine: Int,
    val endLine: Int,
    val label: String,
    val kind: MinimapSectionKind
)

enum class MinimapSectionKind {
    CLASS, FUNCTION, IMPORT, COMMENT, CODE
}

data class MinimapState(
    val isEnabled: Boolean,
    val scale: Float,
    val showSlider: Boolean,
    val maxColumn: Int,
    val sections: List<MinimapSection>
)

interface MinimapRepository {
    fun getMinimapState(): Flow<MinimapState>
    fun getMinimapContent(content: String): Flow<List<String>>
    fun setEnabled(enabled: Boolean)
    fun setScale(scale: Float)
    fun setShowSlider(show: Boolean)
    fun buildSections(content: String, languageId: String): List<MinimapSection>
}
