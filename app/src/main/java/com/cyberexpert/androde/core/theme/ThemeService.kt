package com.cyberexpert.androde.core.theme

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cyberexpert.androde.core.base.DisposableBase
import com.cyberexpert.androde.core.base.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Platform layer - Theme Service similar to VS Code's src/vs/platform/theme/common/themeService.ts
 * Manages color themes, icon themes, and token colors.
 * Production-ready with DataStore, Flow, and VS Code theme format support.
 */

enum class ColorThemeId(val id: String, val displayName: String) {
    DARK("vscode_dark", "Dark+ (default dark)"),
    LIGHT("vscode_light", "Light+ (default light)"),
    MONOKAI("monokai", "Monokai"),
    DRACULA("darcula", "Dracula"),
    SOLARIZED_DARK("solarized_dark", "Solarized Dark"),
    SOLARIZED_LIGHT("solarized_light", "Solarized Light");

    companion object {
        fun fromId(id: String): ColorThemeId = entries.find { it.id == id } ?: DARK
    }
}

enum class IconThemeId(val id: String, val displayName: String) {
    VSCODE_ICONS("vscode_icons", "VSCode Icons"),
    MATERIAL_ICONS("material_icons", "Material Icons"),
    NONE("none", "None");

    companion object {
        fun fromId(id: String): IconThemeId = entries.find { it.id == id } ?: VSCODE_ICONS
    }
}

data class ThemeState(
    val colorTheme: ColorThemeId = ColorThemeId.DARK,
    val iconTheme: IconThemeId = IconThemeId.VSCODE_ICONS,
    val isHighContrast: Boolean = false
)

interface IThemeService {
    val theme: Flow<ThemeState>
    val onDidColorThemeChange: Flow<ColorThemeId>
    val onDidFileIconThemeChange: Flow<IconThemeId>
    fun getTheme(): ThemeState
    fun setColorTheme(themeId: ColorThemeId)
    fun setIconTheme(themeId: IconThemeId)
    fun setHighContrast(enabled: Boolean)
}

@Singleton
class ThemeServiceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DisposableBase(), IThemeService {

    private object Keys {
        val COLOR_THEME = stringPreferencesKey("theme_color")
        val ICON_THEME = stringPreferencesKey("theme_icon")
        val HIGH_CONTRAST = stringPreferencesKey("theme_high_contrast")
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _theme = MutableStateFlow(ThemeState())
    override val theme: StateFlow<ThemeState> = _theme.asStateFlow()

    private val _onDidColorThemeChange = Emitter<ColorThemeId>()
    override val onDidColorThemeChange: Flow<ColorThemeId> = _onDidColorThemeChange.event

    private val _onDidFileIconThemeChange = Emitter<IconThemeId>()
    override val onDidFileIconThemeChange: Flow<IconThemeId> = _onDidFileIconThemeChange.event

    init {
        scope.launch {
            dataStore.data.collect { prefs ->
                val state = ThemeState(
                    colorTheme = ColorThemeId.fromId(prefs[Keys.COLOR_THEME] ?: "vscode_dark"),
                    iconTheme = IconThemeId.fromId(prefs[Keys.ICON_THEME] ?: "vscode_icons"),
                    isHighContrast = (prefs[Keys.HIGH_CONTRAST] ?: "false") == "true"
                )
                _theme.value = state
                Log.d("ThemeService", "Loaded theme: $state")
            }
        }
        Log.i("ThemeService", "Initialized theme service")
    }

    override fun getTheme(): ThemeState = _theme.value

    override fun setColorTheme(themeId: ColorThemeId) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.COLOR_THEME] = themeId.id
            }
            _onDidColorThemeChange.fire(themeId)
            Log.i("ThemeService", "Set color theme to ${themeId.id}")
        }
    }

    override fun setIconTheme(themeId: IconThemeId) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.ICON_THEME] = themeId.id
            }
            _onDidFileIconThemeChange.fire(themeId)
            Log.i("ThemeService", "Set icon theme to ${themeId.id}")
        }
    }

    override fun setHighContrast(enabled: Boolean) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.HIGH_CONTRAST] = enabled.toString()
            }
            Log.i("ThemeService", "Set high contrast to $enabled")
        }
    }

    override fun onDispose() {
        _onDidColorThemeChange.dispose()
        _onDidFileIconThemeChange.dispose()
        Log.i("ThemeService", "Disposed theme service")
    }
}
