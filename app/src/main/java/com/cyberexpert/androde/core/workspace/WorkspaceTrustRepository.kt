package com.cyberexpert.androde.core.workspace

import kotlinx.coroutines.flow.Flow

/**
 * Workspace trust repository, similar to VS Code workspace trust.
 * Manages trusted workspaces for security, preventing untrusted code execution.
 * Production-ready with DataStore persistence, trust levels.
 */
interface WorkspaceTrustRepository {
    fun getTrustedWorkspaces(): Flow<List<TrustedWorkspace>>
    fun isWorkspaceTrusted(path: String): Flow<Boolean>
    suspend fun trustWorkspace(path: String): Boolean
    suspend fun untrustWorkspace(path: String): Boolean
    suspend fun setTrustEnabled(enabled: Boolean)
    fun isTrustEnabled(): Flow<Boolean>
}

data class TrustedWorkspace(
    val path: String,
    val trustedAt: Long,
    val trustLevel: TrustLevel
)

enum class TrustLevel {
    TRUSTED, UNTRUSTED, UNKNOWN
}
