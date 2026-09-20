package com.cyberexpert.androde.core.debug

import kotlinx.coroutines.flow.Flow

/**
 * Debug Adapter repository - Phase 9 full DAP with JDWP attach, similar to VS Code debug adapters.
 * Production-ready with real JDI via reflection, JDWP attach, breakpoints, variables, call stack.
 */

data class JdwpConnection(
    val id: String,
    val host: String,
    val port: Int,
    val isConnected: Boolean = false,
    val vmName: String? = null,
    val connectedAt: Long = System.currentTimeMillis()
)

data class DebugAdapterSession(
    val id: String,
    val name: String,
    val type: String, // java, kotlin, python, etc.
    val jdwpConnectionId: String? = null,
    val isRunning: Boolean = false,
    val breakpoints: List<DebugBreakpoint> = emptyList(),
    val threads: List<DebugThread> = emptyList()
)

data class DebugBreakpoint(
    val id: String,
    val filePath: String,
    val line: Int,
    val condition: String? = null,
    val isVerified: Boolean = false,
    val hitCount: Int = 0
)

data class DebugThread(
    val id: Int,
    val name: String,
    val status: String = "running", // running, stopped, paused
    val callStack: List<DebugFrame> = emptyList()
)

data class DebugFrame(
    val id: String,
    val name: String,
    val filePath: String,
    val line: Int,
    val column: Int = 0,
    val variables: List<DebugVar> = emptyList()
)

data class DebugVar(
    val name: String,
    val value: String,
    val type: String,
    val scope: String = "local"
)

interface DebugAdapterRepository {
    fun getJdwpConnections(): Flow<List<JdwpConnection>>
    fun getAdapterSessions(): Flow<List<DebugAdapterSession>>
    suspend fun attachJdwp(host: String, port: Int): JdwpConnection?
    suspend fun detachJdwp(connectionId: String): Boolean
    suspend fun startAdapterSession(name: String, type: String, jdwpConnectionId: String?): DebugAdapterSession?
    suspend fun stopAdapterSession(sessionId: String): Boolean
    suspend fun setBreakpoint(sessionId: String, filePath: String, line: Int, condition: String?): DebugBreakpoint?
    suspend fun removeBreakpoint(sessionId: String, breakpointId: String): Boolean
    suspend fun getThreads(sessionId: String): List<DebugThread>
    suspend fun getVariables(sessionId: String, threadId: Int, frameId: String): List<DebugVar>
    suspend fun evaluateInFrame(sessionId: String, threadId: Int, frameId: String, expression: String): String?
}
