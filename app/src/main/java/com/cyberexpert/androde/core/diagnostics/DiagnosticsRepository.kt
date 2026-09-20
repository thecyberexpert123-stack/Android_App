package com.cyberexpert.androde.core.diagnostics

import com.cyberexpert.androde.domain.model.ide.Diagnostic
import kotlinx.coroutines.flow.Flow

/**
 * Diagnostics repository, similar to VS Code Problems panel.
 * Collects diagnostics from LSP, linters, compilers.
 */
interface DiagnosticsRepository {
    fun getDiagnostics(): Flow<List<Diagnostic>>
    fun getDiagnosticsForFile(filePath: String): Flow<List<Diagnostic>>
    suspend fun addDiagnostic(diagnostic: Diagnostic)
    suspend fun addDiagnostics(diagnostics: List<Diagnostic>)
    suspend fun clearDiagnostics(filePath: String? = null)
    suspend fun clearAll()
}
