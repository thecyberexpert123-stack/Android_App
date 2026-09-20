package com.cyberexpert.androde.core.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Extension API Advanced - Phase 10 100% REAL WORKING++++++++.
 * Similar to VS Code extension API advanced: authentication, secrets, storage, workspace, window, env.
 * Covers: authentication.getSession, secrets store/get/delete, globalState/workspaceState, workspace folders, window active editor, env.
 */

data class AuthSession(
    val id: String,
    val accessToken: String,
    val accountLabel: String,
    val scopes: List<String>
)

data class SecretItem(
    val key: String,
    val value: String
)

data class ExtensionStorageItem(
    val key: String,
    val value: String,
    val scope: StorageScope
)

enum class StorageScope { GLOBAL, WORKSPACE }

data class WorkspaceFolderInfo(
    val uri: String,
    val name: String,
    val index: Int
)

data class EnvInfo(
    val appName: String,
    val appRoot: String,
    val language: String,
    val machineId: String,
    val sessionId: String
)

interface ExtensionApiAdvanced {
    fun getAuthSessions(): Flow<List<AuthSession>>
    fun getSecrets(): Flow<List<SecretItem>>
    fun getStorageItems(): Flow<List<ExtensionStorageItem>>
    fun getWorkspaceFolders(): Flow<List<WorkspaceFolderInfo>>
    fun getEnvInfo(): Flow<EnvInfo>

    suspend fun getSession(providerId: String, scopes: List<String>, createIfNone: Boolean = false): Result<AuthSession>
    suspend fun storeSecret(key: String, value: String): Result<Unit>
    suspend fun getSecret(key: String): Result<String?>
    suspend fun deleteSecret(key: String): Result<Unit>
    suspend fun setGlobalState(key: String, value: String): Result<Unit>
    suspend fun getGlobalState(key: String): Result<String?>
    suspend fun setWorkspaceState(key: String, value: String): Result<Unit>
    suspend fun getWorkspaceState(key: String): Result<String?>
    suspend fun openWorkspaceFolder(uri: String, name: String): Result<WorkspaceFolderInfo>
}
