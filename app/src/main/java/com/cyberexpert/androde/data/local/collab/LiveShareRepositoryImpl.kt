package com.cyberexpert.androde.data.local.collab

import android.util.Log
import com.cyberexpert.androde.core.collab.LiveShareMessage
import com.cyberexpert.androde.core.collab.LiveShareParticipant
import com.cyberexpert.androde.core.collab.LiveShareRepository
import com.cyberexpert.androde.core.collab.LiveShareSession
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working Live Share repository - Phase 9 with WebRTC concept.
 * Similar to VS Code Live Share, provides session creation, joining, file sharing, messaging, cursor tracking.
 * Production-ready with StateFlow, Dispatchers.IO, Log.
 * Real WebRTC would use org.webrtc:google-webrtc, but for MVP we simulate with StateFlow.
 */
@Singleton
class LiveShareRepositoryImpl @Inject constructor() : LiveShareRepository {

    private val _sessions = MutableStateFlow<List<LiveShareSession>>(emptyList())
    private val _messages = MutableStateFlow<Map<String, List<LiveShareMessage>>>(emptyMap())

    override fun getSessions(): Flow<List<LiveShareSession>> = _sessions.asStateFlow()

    override fun getMessages(sessionId: String): Flow<List<LiveShareMessage>> {
        return _messages.map { it[sessionId] ?: emptyList() }
    }

    override suspend fun createSession(name: String, hostUser: String): LiveShareSession? = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Creating session: $name host: $hostUser")
            if (name.isBlank() || hostUser.isBlank()) {
                Log.w("LiveShareRepo", "Invalid session params")
                return@withContext null
            }

            val sessionId = UUID.randomUUID().toString()
            val hostParticipant = LiveShareParticipant(
                id = UUID.randomUUID().toString(),
                name = hostUser,
                isHost = true
            )

            val session = LiveShareSession(
                id = sessionId,
                name = name,
                hostUser = hostUser,
                isHost = true,
                isActive = true,
                participants = listOf(hostParticipant),
                sharedFiles = emptyList()
            )

            _sessions.value = _sessions.value + session
            _messages.value = _messages.value + (sessionId to emptyList())

            Log.i("LiveShareRepo", "Created session $sessionId name $name host $hostUser")
            session
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to create session $name", e)
            null
        }
    }

    override suspend fun joinSession(sessionId: String, userName: String): LiveShareSession? = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Joining session $sessionId user $userName")
            val session = _sessions.value.find { it.id == sessionId }
            if (session == null) {
                Log.w("LiveShareRepo", "Session not found: $sessionId")
                return@withContext null
            }

            if (!session.isActive) {
                Log.w("LiveShareRepo", "Session not active: $sessionId")
                return@withContext null
            }

            val participant = LiveShareParticipant(
                id = UUID.randomUUID().toString(),
                name = userName,
                isHost = false
            )

            val updatedSession = session.copy(participants = session.participants + participant)
            _sessions.value = _sessions.value.map { s -> if (s.id == sessionId) updatedSession else s }

            Log.i("LiveShareRepo", "User $userName joined session $sessionId")
            updatedSession
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to join session $sessionId", e)
            null
        }
    }

    override suspend fun leaveSession(sessionId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Leaving session $sessionId user $userId")
            val session = _sessions.value.find { it.id == sessionId }
            if (session == null) {
                Log.w("LiveShareRepo", "Session not found: $sessionId")
                return@withContext false
            }

            val updatedParticipants = session.participants.filter { it.id != userId }

            if (updatedParticipants.isEmpty()) {
                // No participants left, deactivate session
                _sessions.value = _sessions.value.filter { it.id != sessionId }
                val mutableMessages = _messages.value.toMutableMap()
                mutableMessages.remove(sessionId)
                _messages.value = mutableMessages
                Log.i("LiveShareRepo", "Session $sessionId ended, no participants left")
            } else {
                val updatedSession = session.copy(participants = updatedParticipants)
                _sessions.value = _sessions.value.map { s -> if (s.id == sessionId) updatedSession else s }
                Log.i("LiveShareRepo", "User $userId left session $sessionId")
            }

            true
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to leave session $sessionId", e)
            false
        }
    }

    override suspend fun shareFile(sessionId: String, filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Sharing file $filePath in session $sessionId")
            val session = _sessions.value.find { it.id == sessionId }
            if (session == null) {
                Log.w("LiveShareRepo", "Session not found: $sessionId")
                return@withContext false
            }

            if (session.sharedFiles.contains(filePath)) {
                Log.w("LiveShareRepo", "File already shared: $filePath")
                return@withContext true
            }

            val updatedSession = session.copy(sharedFiles = session.sharedFiles + filePath)
            _sessions.value = _sessions.value.map { s -> if (s.id == sessionId) updatedSession else s }

            Log.i("LiveShareRepo", "Shared file $filePath in session $sessionId")
            true
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to share file $filePath", e)
            false
        }
    }

    override suspend fun unshareFile(sessionId: String, filePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Unsharing file $filePath in session $sessionId")
            val session = _sessions.value.find { it.id == sessionId }
            if (session == null) {
                Log.w("LiveShareRepo", "Session not found: $sessionId")
                return@withContext false
            }

            val updatedSession = session.copy(sharedFiles = session.sharedFiles.filter { it != filePath })
            _sessions.value = _sessions.value.map { s -> if (s.id == sessionId) updatedSession else s }

            Log.i("LiveShareRepo", "Unshared file $filePath in session $sessionId")
            true
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to unshare file $filePath", e)
            false
        }
    }

    override suspend fun sendMessage(sessionId: String, senderId: String, senderName: String, content: String): LiveShareMessage? = withContext(Dispatchers.IO) {
        try {
            Log.i("LiveShareRepo", "Sending message in $sessionId from $senderName: ${content.take(50)}")
            if (content.isBlank()) {
                Log.w("LiveShareRepo", "Message content blank")
                return@withContext null
            }

            val message = LiveShareMessage(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                senderId = senderId,
                senderName = senderName,
                content = content
            )

            val currentMessages = _messages.value[sessionId] ?: emptyList()
            _messages.value = _messages.value + (sessionId to (currentMessages + message).takeLast(100))

            Log.i("LiveShareRepo", "Sent message ${message.id} in session $sessionId")
            message
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to send message in $sessionId", e)
            null
        }
    }

    override suspend fun updateCursor(sessionId: String, userId: String, filePath: String, line: Int, column: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("LiveShareRepo", "Updating cursor for $userId in $sessionId file $filePath:$line:$column")
            val session = _sessions.value.find { it.id == sessionId }
            if (session == null) {
                Log.w("LiveShareRepo", "Session not found: $sessionId")
                return@withContext false
            }

            val updatedParticipants = session.participants.map { p ->
                if (p.id == userId) p.copy(cursorFile = filePath, cursorLine = line, cursorColumn = column) else p
            }

            val updatedSession = session.copy(participants = updatedParticipants)
            _sessions.value = _sessions.value.map { s -> if (s.id == sessionId) updatedSession else s }

            true
        } catch (e: Exception) {
            Log.e("LiveShareRepo", "Failed to update cursor for $userId", e)
            false
        }
    }
}
