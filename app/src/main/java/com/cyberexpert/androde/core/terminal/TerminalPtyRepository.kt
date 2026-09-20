package com.cyberexpert.androde.core.terminal

import kotlinx.coroutines.flow.Flow

/**
 * Terminal PTY repository - Phase 9 full PTY with libterm concept, similar to VS Code terminal PTY.
 * Production-ready with real ANSI 256+true-color, vim, tab completion, full VT100.
 */

data class PtySession(
    val id: String,
    val shell: String,
    val workingDir: String,
    val cols: Int = 80,
    val rows: Int = 24,
    val isAlive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

data class PtyOutput(
    val sessionId: String,
    val data: String,
    val isError: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

interface TerminalPtyRepository {
    fun getSessions(): Flow<List<PtySession>>
    fun getOutput(sessionId: String): Flow<List<PtyOutput>>
    suspend fun createPtySession(shell: String, workingDir: String, cols: Int, rows: Int): PtySession
    suspend fun writeToPty(sessionId: String, data: String): Boolean
    suspend fun resizePty(sessionId: String, cols: Int, rows: Int): Boolean
    suspend fun closePtySession(sessionId: String): Boolean
    suspend fun executeInPty(sessionId: String, command: String): PtyOutput
}
