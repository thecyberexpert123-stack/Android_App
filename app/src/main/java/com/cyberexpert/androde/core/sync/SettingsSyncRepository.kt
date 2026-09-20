package com.cyberexpert.androde.core.sync

import kotlinx.coroutines.flow.Flow

/**
 * Settings Sync, Profiles, Remote Tunnels - Phase 9, similar to VS Code Settings Sync, Profiles, Remote Tunnels.
 * Production-ready models and interface.
 */

data class SyncProfile(
    val id: String,
    val name: String,
    val description: String? = null,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis()
)

data class SyncSettings(
    val settings: Map<String, String> = emptyMap(),
    val keybindings: List<SyncKeybinding> = emptyList(),
    val extensions: List<String> = emptyList(),
    val snippets: Map<String, String> = emptyMap(),
    val lastSyncedAt: Long = System.currentTimeMillis()
)

data class SyncKeybinding(
    val command: String,
    val key: String,
    val whenClause: String? = null
)

data class RemoteTunnel(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

interface SettingsSyncRepository {
    fun getProfiles(): Flow<List<SyncProfile>>
    fun getCurrentProfile(): Flow<SyncProfile?>
    fun getSyncSettings(): Flow<SyncSettings>
    fun getRemoteTunnels(): Flow<List<RemoteTunnel>>
    suspend fun createProfile(name: String, description: String?): SyncProfile?
    suspend fun deleteProfile(profileId: String): Boolean
    suspend fun switchProfile(profileId: String): Boolean
    suspend fun syncSettings(): Boolean
    suspend fun createTunnel(name: String, host: String, port: Int): RemoteTunnel?
    suspend fun deleteTunnel(tunnelId: String): Boolean
    suspend fun startTunnel(tunnelId: String): Boolean
    suspend fun stopTunnel(tunnelId: String): Boolean
}
