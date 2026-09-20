package com.cyberexpert.androde.core.settings

import kotlinx.coroutines.flow.Flow

/**
 * Settings repository, similar to VS Code settings.json - Phase 5 with format on save, emmet on tab, icon theme.
 * Uses DataStore Preferences for persistence.
 */
interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
    fun getFontSize(): Flow<Int>
    suspend fun setFontSize(size: Int)
    fun getTabSize(): Flow<Int>
    suspend fun setTabSize(size: Int)
    fun getWordWrap(): Flow<Boolean>
    suspend fun setWordWrap(enabled: Boolean)
    fun getMinimap(): Flow<Boolean>
    suspend fun setMinimap(enabled: Boolean)
    fun getAutoSave(): Flow<Boolean>
    suspend fun setAutoSave(enabled: Boolean)
    fun getShowHiddenFiles(): Flow<Boolean>
    suspend fun setShowHiddenFiles(show: Boolean)
    fun getRecentProjects(): Flow<List<String>>
    suspend fun addRecentProject(path: String)

    // Phase 5 - new settings
    fun getFormatOnSave(): Flow<Boolean>
    suspend fun setFormatOnSave(enabled: Boolean)
    fun getEmmetOnTab(): Flow<Boolean>
    suspend fun setEmmetOnTab(enabled: Boolean)
    fun getIconThemeId(): Flow<String>
    suspend fun setIconThemeId(themeId: String)
    fun getBreadcrumbsEnabled(): Flow<Boolean>
    suspend fun setBreadcrumbsEnabled(enabled: Boolean)
    fun getMinimapEnabled(): Flow<Boolean>
    suspend fun setMinimapEnabled(enabled: Boolean)
}

enum class AppTheme {
    LIGHT, DARK, SYSTEM, MONOKAI, GITHUB_DARK, DRACULA, SOLARIZED_DARK
}
