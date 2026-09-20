package com.cyberexpert.androde.core.collab

import kotlinx.coroutines.flow.Flow

/**
 * Live Share repository - Phase 9, similar to VS Code Live Share with WebRTC concept.
 * Production-ready models and interface.
 */

data class LiveShareSession(
    val id: String,
    val name: String,
    val hostUser: String,
    val isHost: Boolean = false,
    val isActive: Boolean = true,
    val participants: List<LiveShareParticipant> = emptyList(),
    val sharedFiles: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class LiveShareParticipant(
    val id: String,
    val name: String,
    val email: String? = null,
    val isHost: Boolean = false,
    val cursorFile: String? = null,
    val cursorLine: Int = 0,
    val cursorColumn: Int = 0
)

data class LiveShareMessage(
    val id: String,
    val sessionId: String,
    val senderId: String,
    val senderName: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

interface LiveShareRepository {
    fun getSessions(): Flow<List<LiveShareSession>>
    fun getMessages(sessionId: String): Flow<List<LiveShareMessage>>
    suspend fun createSession(name: String, hostUser: String): LiveShareSession?
    suspend fun joinSession(sessionId: String, userName: String): LiveShareSession?
    suspend fun leaveSession(sessionId: String, userId: String): Boolean
    suspend fun shareFile(sessionId: String, filePath: String): Boolean
    suspend fun unshareFile(sessionId: String, filePath: String): Boolean
    suspend fun sendMessage(sessionId: String, senderId: String, senderName: String, content: String): LiveShareMessage?
    suspend fun updateCursor(sessionId: String, userId: String, filePath: String, line: Int, column: Int): Boolean
}
