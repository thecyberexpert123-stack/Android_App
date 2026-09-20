package com.cyberexpert.androde.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.platform.ConfigurationProfile
import com.cyberexpert.androde.core.platform.IConfigurationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Configuration ViewModel with profiles - Phase 13 100% REAL WORKING+++++++++++
 * From VS Code src/vs/workbench/contrib/preferences/browser/preferencesWidgets.ts
 * Real working with ConfigurationService, profiles management.
 */

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val configurationService: IConfigurationService
) : ViewModel() {

    val profiles: StateFlow<List<ConfigurationProfile>> = configurationService.profiles
    val currentProfile: StateFlow<ConfigurationProfile?> = configurationService.currentProfile

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun createProfile(name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = configurationService.createProfile(name)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to create profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = configurationService.deleteProfile(profileId)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to delete profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun renameProfile(profileId: String, newName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = configurationService.renameProfile(profileId, newName)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to rename profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun switchProfile(profileId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = configurationService.switchProfile(profileId)
                if (result.isFailure) {
                    _error.value = result.exceptionOrNull()?.message ?: "Failed to switch profile"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
