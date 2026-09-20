package com.cyberexpert.androde.presentation.screens.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.extensions.ExtensionRepository
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
class ExtensionsViewModel @Inject constructor(
    private val extensionRepository: ExtensionRepository
) : ViewModel() {

    val installedExtensions = extensionRepository.getInstalledExtensions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val marketplaceExtensions = extensionRepository.getMarketplaceExtensions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Extension>>(emptyList())
    val searchResults: StateFlow<List<Extension>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadExtensions()
    }

    fun loadExtensions() {
        viewModelScope.launch {
            _isLoading.value = true
            extensionRepository.loadBuiltinExtensions()
            _isLoading.value = false
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _searchResults.value = extensionRepository.searchMarketplace(query)
        }
    }

    fun install(extension: Extension) {
        viewModelScope.launch {
            extensionRepository.installExtension(extension)
        }
    }

    fun uninstall(extensionId: String) {
        viewModelScope.launch {
            extensionRepository.uninstallExtension(extensionId)
        }
    }

    fun toggleEnable(extensionId: String, enable: Boolean) {
        viewModelScope.launch {
            extensionRepository.enableExtension(extensionId, enable)
        }
    }
}
