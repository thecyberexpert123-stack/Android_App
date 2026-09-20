package com.cyberexpert.androde.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.settings.AppTheme
import com.cyberexpert.androde.core.settings.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val theme = settingsRepository.getTheme().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppTheme.SYSTEM)
    val fontSize = settingsRepository.getFontSize().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 14)
    val tabSize = settingsRepository.getTabSize().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 4)
    val wordWrap = settingsRepository.getWordWrap().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val minimap = settingsRepository.getMinimap().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val autoSave = settingsRepository.getAutoSave().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val showHidden = settingsRepository.getShowHiddenFiles().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    // Phase 5
    val formatOnSave = settingsRepository.getFormatOnSave().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val emmetOnTab = settingsRepository.getEmmetOnTab().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val iconThemeId = settingsRepository.getIconThemeId().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "vscode_icons")
    val breadcrumbsEnabled = settingsRepository.getBreadcrumbsEnabled().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val minimapEnabled = settingsRepository.getMinimapEnabled().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setTheme(theme: AppTheme) = viewModelScope.launch { settingsRepository.setTheme(theme) }
    fun setFontSize(size: Int) = viewModelScope.launch { settingsRepository.setFontSize(size) }
    fun setTabSize(size: Int) = viewModelScope.launch { settingsRepository.setTabSize(size) }
    fun setWordWrap(enabled: Boolean) = viewModelScope.launch { settingsRepository.setWordWrap(enabled) }
    fun setMinimap(enabled: Boolean) = viewModelScope.launch { settingsRepository.setMinimap(enabled) }
    fun setAutoSave(enabled: Boolean) = viewModelScope.launch { settingsRepository.setAutoSave(enabled) }
    fun setShowHidden(show: Boolean) = viewModelScope.launch { settingsRepository.setShowHiddenFiles(show) }
    // Phase 5
    fun setFormatOnSave(enabled: Boolean) = viewModelScope.launch { settingsRepository.setFormatOnSave(enabled) }
    fun setEmmetOnTab(enabled: Boolean) = viewModelScope.launch { settingsRepository.setEmmetOnTab(enabled) }
    fun setIconThemeId(themeId: String) = viewModelScope.launch { settingsRepository.setIconThemeId(themeId) }
    fun setBreadcrumbsEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setBreadcrumbsEnabled(enabled) }
    fun setMinimapEnabled(enabled: Boolean) = viewModelScope.launch { settingsRepository.setMinimapEnabled(enabled) }
}
