package com.cyberexpert.androde.presentation.screens.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.debug.DebugRepository
import com.cyberexpert.androde.domain.model.ide.DebugSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DebugViewModel @Inject constructor(
    private val debugRepository: DebugRepository
) : ViewModel() {

    val sessions = debugRepository.getSessions().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val activeSession = debugRepository.getActiveSession().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    val breakpoints = debugRepository.getBreakpoints().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun startDebugSession() {
        viewModelScope.launch {
            val session = DebugSession(name = "Debug Session", type = "kotlin")
            debugRepository.startSession(session)
        }
    }

    fun stopSession(sessionId: String) {
        viewModelScope.launch {
            debugRepository.stopSession(sessionId)
        }
    }

    fun removeBreakpoint(id: String) {
        viewModelScope.launch {
            debugRepository.removeBreakpoint(id)
        }
    }

    fun toggleBreakpoint(filePath: String, line: Int) {
        viewModelScope.launch {
            debugRepository.toggleBreakpoint(filePath, line)
        }
    }
}
