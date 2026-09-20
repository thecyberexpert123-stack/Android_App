package com.cyberexpert.androde.presentation.screens.icontheme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.extensions.IconTheme
import com.cyberexpert.androde.core.extensions.IconThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IconThemeViewModel @Inject constructor(
    private val iconThemeRepository: IconThemeRepository
) : ViewModel() {

    val themes: StateFlow<List<IconTheme>> = iconThemeRepository.getAvailableThemes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentTheme: StateFlow<IconTheme?> = iconThemeRepository.getCurrentTheme()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadThemes() {
        viewModelScope.launch {
            iconThemeRepository.loadThemes()
        }
    }

    fun setTheme(themeId: String) {
        viewModelScope.launch {
            iconThemeRepository.setTheme(themeId)
        }
    }
}
