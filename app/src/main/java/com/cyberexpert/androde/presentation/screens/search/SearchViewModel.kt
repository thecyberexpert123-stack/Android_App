package com.cyberexpert.androde.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.search.SearchProgress
import com.cyberexpert.androde.core.search.SearchRepository
import com.cyberexpert.androde.domain.model.ide.SearchQuery
import com.cyberexpert.androde.domain.model.ide.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow(SearchQuery(""))
    val query: StateFlow<SearchQuery> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<SearchResult>>(emptyList())
    val results: StateFlow<List<SearchResult>> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _totalMatches = MutableStateFlow(0)
    val totalMatches: StateFlow<Int> = _totalMatches.asStateFlow()

    fun updateQuery(newQuery: String) {
        _query.value = _query.value.copy(query = newQuery)
    }

    fun toggleRegex() {
        _query.value = _query.value.copy(isRegex = !_query.value.isRegex)
    }

    fun toggleCaseSensitive() {
        _query.value = _query.value.copy(isCaseSensitive = !_query.value.isCaseSensitive)
    }

    fun toggleWholeWord() {
        _query.value = _query.value.copy(isWholeWord = !_query.value.isWholeWord)
    }

    fun search(root: File) {
        if (_query.value.query.isBlank()) return
        viewModelScope.launch {
            _isSearching.value = true
            _results.value = emptyList()
            _totalMatches.value = 0

            searchRepository.search(_query.value, root).collect { progress ->
                when (progress) {
                    is SearchProgress.ResultFound -> {
                        _results.value = _results.value + progress.result
                    }
                    is SearchProgress.Completed -> {
                        _totalMatches.value = progress.totalMatches
                        _isSearching.value = false
                    }
                    is SearchProgress.Error -> {
                        _isSearching.value = false
                    }
                    else -> {}
                }
            }
        }
    }

    fun clearResults() {
        _results.value = emptyList()
        _totalMatches.value = 0
    }
}
