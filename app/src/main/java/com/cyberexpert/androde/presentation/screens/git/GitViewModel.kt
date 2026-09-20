package com.cyberexpert.androde.presentation.screens.git

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.git.GitRepository
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.GitCommit
import com.cyberexpert.androde.domain.model.ide.GitRepository as GitRepoModel
import com.cyberexpert.androde.domain.model.ide.GitStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class GitViewModel @Inject constructor(
    private val gitRepository: GitRepository
) : ViewModel() {

    private val _repository = MutableStateFlow<GitRepoModel?>(null)
    val repository: StateFlow<GitRepoModel?> = _repository.asStateFlow()

    private val _status = MutableStateFlow<GitStatus?>(null)
    val status: StateFlow<GitStatus?> = _status.asStateFlow()

    private val _commits = MutableStateFlow<List<GitCommit>>(emptyList())
    val commits: StateFlow<List<GitCommit>> = _commits.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun openRepository(root: File) {
        viewModelScope.launch {
            _isLoading.value = true
            when (val result = gitRepository.openRepository(root)) {
                is AppResult.Success -> {
                    _repository.value = result.data
                    _status.value = result.data.status
                    loadCommits()
                }
                is AppResult.Error -> _error.value = result.error.userMessage
                else -> {}
            }
            _isLoading.value = false
        }
    }

    fun refreshStatus() {
        viewModelScope.launch {
            val repo = _repository.value ?: return@launch
            when (val result = gitRepository.getStatus(repo)) {
                is AppResult.Success -> _status.value = result.data
                is AppResult.Error -> _error.value = result.error.userMessage
                else -> {}
            }
        }
    }

    private fun loadCommits() {
        viewModelScope.launch {
            val repo = _repository.value ?: return@launch
            when (val result = gitRepository.getCommits(repo, 50)) {
                is AppResult.Success -> _commits.value = result.data
                is AppResult.Error -> _error.value = result.error.userMessage
                else -> {}
            }
        }
    }

    fun stageAll() {
        viewModelScope.launch {
            val repo = _repository.value ?: return@launch
            gitRepository.stageAll(repo)
            refreshStatus()
        }
    }

    fun commit(message: String) {
        viewModelScope.launch {
            val repo = _repository.value ?: return@launch
            if (message.isBlank()) {
                _error.value = "Commit message cannot be blank"
                return@launch
            }
            when (val result = gitRepository.commit(repo, message)) {
                is AppResult.Success -> {
                    refreshStatus()
                    loadCommits()
                }
                is AppResult.Error -> _error.value = result.error.userMessage
                else -> {}
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
