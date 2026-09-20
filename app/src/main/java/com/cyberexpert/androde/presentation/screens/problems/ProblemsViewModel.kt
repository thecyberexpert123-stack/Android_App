package com.cyberexpert.androde.presentation.screens.problems

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberexpert.androde.core.diagnostics.DiagnosticsRepository
import com.cyberexpert.androde.domain.model.ide.Diagnostic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProblemsViewModel @Inject constructor(
    private val diagnosticsRepository: DiagnosticsRepository
) : ViewModel() {

    val diagnostics = diagnosticsRepository.getDiagnostics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAll() {
        viewModelScope.launch {
            diagnosticsRepository.clearAll()
        }
    }

    fun addSampleDiagnostics() {
        viewModelScope.launch {
            diagnosticsRepository.addDiagnostics(
                listOf(
                    Diagnostic(
                        filePath = "/sample/Main.kt",
                        fileName = "Main.kt",
                        line = 10,
                        column = 5,
                        severity = com.cyberexpert.androde.domain.model.ide.DiagnosticSeverity.ERROR,
                        message = "Unresolved reference: foo",
                        source = "Kotlin"
                    ),
                    Diagnostic(
                        filePath = "/sample/Main.kt",
                        fileName = "Main.kt",
                        line = 15,
                        column = 10,
                        severity = com.cyberexpert.androde.domain.model.ide.DiagnosticSeverity.WARNING,
                        message = "Variable 'bar' is never used",
                        source = "Kotlin"
                    )
                )
            )
        }
    }
}
