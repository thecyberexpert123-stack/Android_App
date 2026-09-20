package com.cyberexpert.androde.presentation.screens.extensionhost

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.extensions.ExtensionHost
import com.cyberexpert.androde.core.extensions.RunningExtension
import com.cyberexpert.androde.domain.model.ide.Extension
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExtensionHostViewModel @Inject constructor(
    private val extensionHost: ExtensionHost
) : ViewModel() {

    val runningExtensions: StateFlow<List<RunningExtension>> = extensionHost.getRunningExtensions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _lastResult = MutableStateFlow<String>("")
    val lastResult: StateFlow<String> = _lastResult.asStateFlow()

    fun activateExtension(extension: Extension) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = extensionHost.activateExtension(extension)
            _lastResult.value = if (result.success) "Activated ${result.extensionId}" else "Failed: ${result.error}"
            _isLoading.value = false
        }
    }

    fun deactivateExtension(extensionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = extensionHost.deactivateExtension(extensionId)
            _lastResult.value = if (success) "Deactivated $extensionId" else "Failed to deactivate $extensionId"
            _isLoading.value = false
        }
    }

    fun executeCommand(extensionId: String, command: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = extensionHost.executeCommand(extensionId, command)
            _lastResult.value = if (result.success) "Executed: ${result.result}" else "Failed: ${result.error}"
            _isLoading.value = false
        }
    }
}
