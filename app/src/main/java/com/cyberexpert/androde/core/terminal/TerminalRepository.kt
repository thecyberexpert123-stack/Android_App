package com.cyberexpert.androde.core.terminal

import com.cyberexpert.androde.domain.model.ide.TerminalSession
import kotlinx.coroutines.flow.Flow

interface TerminalRepository {
    fun getSessions(): Flow<List<TerminalSession>>
    fun getActiveSession(): Flow<TerminalSession?>
    suspend fun createSession(workingDir: String, shell: String = "/system/bin/sh"): TerminalSession
    suspend fun closeSession(sessionId: String)
    suspend fun executeCommand(sessionId: String, command: String): Flow<String>
    suspend fun setActiveSession(sessionId: String)
    suspend fun clearSession(sessionId: String)
}
