package com.cyberexpert.androde.data.local.workspace

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.cyberexpert.androde.core.workspace.TrustedWorkspace
import com.cyberexpert.androde.core.workspace.TrustLevel
import com.cyberexpert.androde.core.workspace.WorkspaceTrustRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working workspace trust repository - Phase 6.
 * Similar to VS Code workspace trust, manages trusted folders for security.
 * Uses DataStore for persistence, Flow for reactive, security checks.
 */
@Singleton
class WorkspaceTrustRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : WorkspaceTrustRepository {

    private object Keys {
        val TRUSTED_WORKSPACES = stringSetPreferencesKey("trusted_workspaces")
        val TRUST_ENABLED = booleanPreferencesKey("trust_enabled")
    }

    private val _trustedWorkspaces = MutableStateFlow<List<TrustedWorkspace>>(emptyList())

    override fun getTrustedWorkspaces(): Flow<List<TrustedWorkspace>> {
        return dataStore.data.map { prefs ->
            val paths = prefs[Keys.TRUSTED_WORKSPACES] ?: emptySet()
            paths.map { path ->
                TrustedWorkspace(
                    path = path,
                    trustedAt = System.currentTimeMillis(),
                    trustLevel = TrustLevel.TRUSTED
                )
            }
        }
    }

    override fun isWorkspaceTrusted(path: String): Flow<Boolean> {
        return dataStore.data.map { prefs ->
            val trusted = prefs[Keys.TRUSTED_WORKSPACES] ?: emptySet()
            val enabled = prefs[Keys.TRUST_ENABLED] ?: true
            if (!enabled) true else trusted.contains(path)
        }
    }

    override suspend fun trustWorkspace(path: String): Boolean {
        return try {
            dataStore.edit { prefs ->
                val current = prefs[Keys.TRUSTED_WORKSPACES]?.toMutableSet() ?: mutableSetOf()
                current.add(path)
                prefs[Keys.TRUSTED_WORKSPACES] = current
            }
            Log.i("WorkspaceTrust", "Trusted workspace: $path")
            true
        } catch (e: Exception) {
            Log.e("WorkspaceTrust", "Failed to trust workspace: $path", e)
            false
        }
    }

    override suspend fun untrustWorkspace(path: String): Boolean {
        return try {
            dataStore.edit { prefs ->
                val current = prefs[Keys.TRUSTED_WORKSPACES]?.toMutableSet() ?: mutableSetOf()
                current.remove(path)
                prefs[Keys.TRUSTED_WORKSPACES] = current
            }
            Log.i("WorkspaceTrust", "Untrusted workspace: $path")
            true
        } catch (e: Exception) {
            Log.e("WorkspaceTrust", "Failed to untrust workspace: $path", e)
            false
        }
    }

    override suspend fun setTrustEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.TRUST_ENABLED] = enabled }
        Log.i("WorkspaceTrust", "Trust enabled: $enabled")
    }

    override fun isTrustEnabled(): Flow<Boolean> {
        return dataStore.data.map { it[Keys.TRUST_ENABLED] ?: true }
    }
}
