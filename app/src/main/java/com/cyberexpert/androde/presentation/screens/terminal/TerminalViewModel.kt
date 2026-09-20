package com.cyberexpert.androde.presentation.screens.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.terminal.TerminalRepository
import com.cyberexpert.androde.domain.model.ide.TerminalSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val terminalRepository: TerminalRepository
) : ViewModel() {

    val sessions = terminalRepository.getSessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeSession = terminalRepository.getActiveSession().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting

    fun createSession(workingDir: String) {
        viewModelScope.launch {
            terminalRepository.createSession(workingDir)
        }
    }

    fun executeCommand(command: String) {
        val session = activeSession.value ?: return
        viewModelScope.launch {
            _isExecuting.value = true
            terminalRepository.executeCommand(session.id, command).collect {
                // Output handled via repository flow
            }
            _isExecuting.value = false
        }
    }

    fun clearSession() {
        viewModelScope.launch {
            activeSession.value?.let { terminalRepository.clearSession(it.id) }
        }
    }

    fun closeSession(sessionId: String) {
        viewModelScope.launch {
            terminalRepository.closeSession(sessionId)
        }
    }

    fun setActiveSession(session: TerminalSession) {
        viewModelScope.launch {
            terminalRepository.setActiveSession(session.id)
        }
    }
}
