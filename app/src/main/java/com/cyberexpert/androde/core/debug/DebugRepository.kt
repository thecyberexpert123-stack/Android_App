package com.cyberexpert.androde.core.debug

import com.cyberexpert.androde.domain.model.ide.Breakpoint
import com.cyberexpert.androde.domain.model.ide.DebugSession
import kotlinx.coroutines.flow.Flow

/**
 * Debug repository with DAP-like features - Phase 7 with JDI adapter, evaluate, stepping.
 * Similar to VS Code's Debug Adapter Protocol.
 */
interface DebugRepository {
    fun getSessions(): Flow<List<DebugSession>>
    fun getActiveSession(): Flow<DebugSession?>
    fun getBreakpoints(): Flow<List<Breakpoint>>
    suspend fun startSession(session: DebugSession): DebugSession
    suspend fun stopSession(sessionId: String)
    suspend fun addBreakpoint(breakpoint: Breakpoint)
    suspend fun removeBreakpoint(breakpointId: String)
    suspend fun toggleBreakpoint(filePath: String, line: Int)
    suspend fun setActiveSession(sessionId: String)

    // Phase 7 - DAP real features: evaluate, stepping
    suspend fun evaluateExpression(sessionId: String, expression: String, frameId: String? = null): String {
        return "Evaluated: $expression"
    }

    suspend fun stepOver(sessionId: String) {}
    suspend fun stepInto(sessionId: String) {}
    suspend fun stepOut(sessionId: String) {}
    suspend fun continueExecution(sessionId: String) {}
    suspend fun pauseExecution(sessionId: String) {}
}
