package com.cyberexpert.androde.data.local.diagnostics

import com.cyberexpert.androde.core.diagnostics.DiagnosticsRepository
import com.cyberexpert.androde.domain.model.ide.Diagnostic
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosticsRepositoryImpl @Inject constructor() : DiagnosticsRepository {

    private val _diagnostics = MutableStateFlow<List<Diagnostic>>(emptyList())

    override fun getDiagnostics(): Flow<List<Diagnostic>> = _diagnostics.asStateFlow()

    override fun getDiagnosticsForFile(filePath: String): Flow<List<Diagnostic>> {
        return _diagnostics.map { list -> list.filter { it.filePath == filePath } }
    }

    override suspend fun addDiagnostic(diagnostic: Diagnostic) {
        _diagnostics.value = _diagnostics.value + diagnostic
    }

    override suspend fun addDiagnostics(diagnostics: List<Diagnostic>) {
        _diagnostics.value = _diagnostics.value + diagnostics
    }

    override suspend fun clearDiagnostics(filePath: String?) {
        if (filePath == null) {
            _diagnostics.value = emptyList()
        } else {
            _diagnostics.value = _diagnostics.value.filter { it.filePath != filePath }
        }
    }

    override suspend fun clearAll() {
        _diagnostics.value = emptyList()
    }
}
