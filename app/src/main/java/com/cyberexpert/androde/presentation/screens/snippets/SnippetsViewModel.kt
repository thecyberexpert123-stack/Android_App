package com.cyberexpert.androde.presentation.screens.snippets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.extensions.Snippet
import com.cyberexpert.androde.core.extensions.SnippetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SnippetsViewModel @Inject constructor(
    private val snippetRepository: SnippetRepository
) : ViewModel() {

    private val _snippets = MutableStateFlow<List<Snippet>>(emptyList())
    val snippets: StateFlow<List<Snippet>> = _snippets.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            _isLoading.value = true
            snippetRepository.loadSnippets()
            _isLoading.value = false
        }
    }

    fun loadSnippetsForLanguage(languageId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                snippetRepository.getSnippetsForLanguage(languageId).collect { list ->
                    _snippets.value = list
                }
            } catch (e: Exception) {
                // Fallback to search
                _snippets.value = snippetRepository.searchSnippets("", languageId)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchSnippets(query: String, languageId: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _snippets.value = snippetRepository.searchSnippets(query, languageId)
            _isLoading.value = false
        }
    }
}
