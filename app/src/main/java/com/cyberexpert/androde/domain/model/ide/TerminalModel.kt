package com.cyberexpert.androde.domain.model.ide

import java.util.UUID

/**
 * Terminal session, similar to VS Code integrated terminal.
 */
data class TerminalSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Terminal",
    val shell: String = "/system/bin/sh",
    val workingDirectory: String = "",
    val isRunning: Boolean = false,
    val exitCode: Int? = null,
    val output: List<TerminalLine> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class TerminalLine(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: TerminalLineType = TerminalLineType.OUTPUT,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TerminalLineType {
    INPUT, OUTPUT, ERROR, SYSTEM
}

data class TerminalState(
    val sessions: List<TerminalSession> = emptyList(),
    val activeSessionId: String? = null,
    val maxSessions: Int = 5
) {
    val activeSession: TerminalSession? get() = sessions.find { it.id == activeSessionId }
}
