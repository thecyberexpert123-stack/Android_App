package com.cyberexpert.androde.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.core.ui.UiState
import com.cyberexpert.androde.domain.usecase.GetUsersUseCase
import com.cyberexpert.androde.domain.usecase.RefreshUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Home screen.
 * Exposes UiState via StateFlow, handles refresh with explicit error handling.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    getUsersUseCase: GetUsersUseCase,
    private val refreshUsersUseCase: RefreshUsersUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    val uiState: StateFlow<UiState<List<com.cyberexpert.androde.domain.model.User>>> =
        getUsersUseCase()
            .map { users ->
                if (users.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Success(users)
                }
            }
            .catch { e ->
                emit(UiState.Error(e.message ?: "Failed to load users"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            when (val result = refreshUsersUseCase()) {
                is AppResult.Success -> {
                    // Data will auto-update via Flow from Room
                    _errorMessage.value = null
                }
                is AppResult.Error -> {
                    _errorMessage.value = result.error.userMessage
                }
                is AppResult.Loading -> {
                    // no-op
                }
            }
            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
