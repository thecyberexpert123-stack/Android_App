package com.cyberexpert.androde.data.local.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.cyberexpert.androde.core.settings.AppTheme
import com.cyberexpert.androde.core.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val FONT_SIZE = intPreferencesKey("font_size")
        val TAB_SIZE = intPreferencesKey("tab_size")
        val WORD_WRAP = booleanPreferencesKey("word_wrap")
        val MINIMAP = booleanPreferencesKey("minimap")
        val AUTO_SAVE = booleanPreferencesKey("auto_save")
        val SHOW_HIDDEN = booleanPreferencesKey("show_hidden")
        val RECENT_PROJECTS = stringSetPreferencesKey("recent_projects")
        // Phase 5
        val FORMAT_ON_SAVE = booleanPreferencesKey("format_on_save")
        val EMMET_ON_TAB = booleanPreferencesKey("emmet_on_tab")
        val ICON_THEME_ID = stringPreferencesKey("icon_theme_id")
        val BREADCRUMBS_ENABLED = booleanPreferencesKey("breadcrumbs_enabled")
        val MINIMAP_ENABLED = booleanPreferencesKey("minimap_enabled")
    }

    override fun getTheme(): Flow<AppTheme> = dataStore.data.map { prefs ->
        val name = prefs[Keys.THEME] ?: AppTheme.SYSTEM.name
        try { AppTheme.valueOf(name) } catch (e: Exception) { AppTheme.SYSTEM }
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[Keys.THEME] = theme.name }
    }

    override fun getFontSize(): Flow<Int> = dataStore.data.map { it[Keys.FONT_SIZE] ?: 14 }
    override suspend fun setFontSize(size: Int) { dataStore.edit { it[Keys.FONT_SIZE] = size.coerceIn(8, 32) } }

    override fun getTabSize(): Flow<Int> = dataStore.data.map { it[Keys.TAB_SIZE] ?: 4 }
    override suspend fun setTabSize(size: Int) { dataStore.edit { it[Keys.TAB_SIZE] = size.coerceIn(1, 8) } }

    override fun getWordWrap(): Flow<Boolean> = dataStore.data.map { it[Keys.WORD_WRAP] ?: false }
    override suspend fun setWordWrap(enabled: Boolean) { dataStore.edit { it[Keys.WORD_WRAP] = enabled } }

    override fun getMinimap(): Flow<Boolean> = dataStore.data.map { it[Keys.MINIMAP] ?: true }
    override suspend fun setMinimap(enabled: Boolean) { dataStore.edit { it[Keys.MINIMAP] = enabled } }

    override fun getAutoSave(): Flow<Boolean> = dataStore.data.map { it[Keys.AUTO_SAVE] ?: false }
    override suspend fun setAutoSave(enabled: Boolean) { dataStore.edit { it[Keys.AUTO_SAVE] = enabled } }

    override fun getShowHiddenFiles(): Flow<Boolean> = dataStore.data.map { it[Keys.SHOW_HIDDEN] ?: false }
    override suspend fun setShowHiddenFiles(show: Boolean) { dataStore.edit { it[Keys.SHOW_HIDDEN] = show } }

    override fun getRecentProjects(): Flow<List<String>> = dataStore.data.map { it[Keys.RECENT_PROJECTS]?.toList() ?: emptyList() }

    override suspend fun addRecentProject(path: String) {
        dataStore.edit { prefs ->
            val current = prefs[Keys.RECENT_PROJECTS]?.toMutableSet() ?: mutableSetOf()
            current.add(path)
            if (current.size > 10) {
                val toRemove = current.size - 10
                current.take(toRemove).forEach { current.remove(it) }
            }
            prefs[Keys.RECENT_PROJECTS] = current
        }
    }

    // Phase 5
    override fun getFormatOnSave(): Flow<Boolean> = dataStore.data.map { it[Keys.FORMAT_ON_SAVE] ?: false }
    override suspend fun setFormatOnSave(enabled: Boolean) { dataStore.edit { it[Keys.FORMAT_ON_SAVE] = enabled } }

    override fun getEmmetOnTab(): Flow<Boolean> = dataStore.data.map { it[Keys.EMMET_ON_TAB] ?: true }
    override suspend fun setEmmetOnTab(enabled: Boolean) { dataStore.edit { it[Keys.EMMET_ON_TAB] = enabled } }

    override fun getIconThemeId(): Flow<String> = dataStore.data.map { it[Keys.ICON_THEME_ID] ?: "vscode_icons" }
    override suspend fun setIconThemeId(themeId: String) { dataStore.edit { it[Keys.ICON_THEME_ID] = themeId } }

    override fun getBreadcrumbsEnabled(): Flow<Boolean> = dataStore.data.map { it[Keys.BREADCRUMBS_ENABLED] ?: true }
    override suspend fun setBreadcrumbsEnabled(enabled: Boolean) { dataStore.edit { it[Keys.BREADCRUMBS_ENABLED] = enabled } }

    override fun getMinimapEnabled(): Flow<Boolean> = dataStore.data.map { it[Keys.MINIMAP_ENABLED] ?: true }
    override suspend fun setMinimapEnabled(enabled: Boolean) { dataStore.edit { it[Keys.MINIMAP_ENABLED] = enabled } }
}
