package com.cyberexpert.androde.core.platform

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.cyberexpert.androde.core.base.DisposableBase
import com.cyberexpert.androde.core.base.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * VS Code Configuration Service - Platform Layer.
 * From VS Code src/vs/platform/configuration/common/configuration.ts
 * Manages settings with profiles, similar to VS Code's IConfigurationService.
 * Real working with DataStore persistence, profiles, change events.
 */

data class ConfigurationProfile(
    val id: String,
    val name: String,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long = System.currentTimeMillis()
)

data class ConfigurationValue(
    val key: String,
    val value: String,
    val profileId: String = "default",
    val scope: ConfigurationScope = ConfigurationScope.USER
)

enum class ConfigurationScope {
    APPLICATION,
    WINDOW,
    RESOURCE,
    USER,
    WORKSPACE,
    WORKSPACE_FOLDER,
    DEFAULT
}

data class ConfigurationChangeEvent(
    val keys: List<String>,
    val profileId: String,
    val scope: ConfigurationScope
)

interface IConfigurationService {
    val onDidChangeConfiguration: Flow<ConfigurationChangeEvent>
    val profiles: StateFlow<List<ConfigurationProfile>>
    val currentProfile: StateFlow<ConfigurationProfile?>

    fun getValue(key: String, profileId: String? = null): Flow<String?>
    suspend fun getValueSync(key: String, profileId: String? = null): String?
    suspend fun updateValue(key: String, value: String, profileId: String? = null, scope: ConfigurationScope = ConfigurationScope.USER): Result<Unit>
    suspend fun deleteValue(key: String, profileId: String? = null): Result<Unit>

    suspend fun createProfile(name: String): Result<ConfigurationProfile>
    suspend fun deleteProfile(profileId: String): Result<Unit>
    suspend fun renameProfile(profileId: String, newName: String): Result<Unit>
    suspend fun switchProfile(profileId: String): Result<Unit>
    fun getProfile(profileId: String): ConfigurationProfile?

    fun inspect(key: String): ConfigurationValue?
    fun keys(): List<String>
}

@Singleton
class ConfigurationServiceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DisposableBase(), IConfigurationService {

    companion object {
        private const val TAG = "ConfigurationService"
        private val KEY_CURRENT_PROFILE = stringPreferencesKey("config_current_profile")
        private val KEY_PROFILES = stringSetPreferencesKey("config_profiles")
        private val KEY_PREFIX = "config_"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _profiles = MutableStateFlow(
        listOf(ConfigurationProfile(id = "default", name = "Default", isDefault = true))
    )
    override val profiles: StateFlow<List<ConfigurationProfile>> = _profiles.asStateFlow()

    private val _currentProfile = MutableStateFlow<ConfigurationProfile?>(
        ConfigurationProfile(id = "default", name = "Default", isDefault = true)
    )
    override val currentProfile: StateFlow<ConfigurationProfile?> = _currentProfile.asStateFlow()

    private val _onDidChangeConfiguration = Emitter<ConfigurationChangeEvent>()
    override val onDidChangeConfiguration: Flow<ConfigurationChangeEvent> = _onDidChangeConfiguration.event

    private val configCache = mutableMapOf<String, ConfigurationValue>()

    init {
        scope.launch {
            try {
                val prefs = dataStore.data.first()
                val currentId = prefs[KEY_CURRENT_PROFILE] ?: "default"
                val profilesSet = prefs[KEY_PROFILES] ?: setOf("default:Default:true")

                val loadedProfiles = profilesSet.mapNotNull { entry ->
                    try {
                        val parts = entry.split(":")
                        if (parts.size >= 3) {
                            ConfigurationProfile(
                                id = parts[0],
                                name = parts[1],
                                isDefault = parts[2].toBoolean(),
                                createdAt = parts.getOrNull(3)?.toLongOrNull() ?: System.currentTimeMillis(),
                                lastUsedAt = parts.getOrNull(4)?.toLongOrNull() ?: System.currentTimeMillis()
                            )
                        } else null
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to parse profile: $entry", e)
                        null
                    }
                }

                if (loadedProfiles.isNotEmpty()) {
                    _profiles.value = loadedProfiles
                    _currentProfile.value = loadedProfiles.find { it.id == currentId } ?: loadedProfiles.first()
                }

                // Load config values
                prefs.asMap().forEach { (key, value) ->
                    val keyName = key.name
                    if (keyName.startsWith(KEY_PREFIX) && !keyName.startsWith("config_profiles") && !keyName.startsWith("config_current_profile") && !keyName.startsWith("config_")) {
                        // Old logic: actually config_ prefix is for all config values
                    }
                    if (keyName.startsWith("config_") && keyName != "config_current_profile" && keyName != "config_profiles" && !keyName.startsWith("config_profile_")) {
                        val configKey = keyName.removePrefix("config_")
                        if (configKey.isNotBlank()) {
                            configCache[configKey] = ConfigurationValue(
                                key = configKey,
                                value = value.toString(),
                                profileId = currentId
                            )
                        }
                    }
                }

                Log.i(TAG, "Loaded ${_profiles.value.size} profiles, current: ${_currentProfile.value?.name}, ${configCache.size} config values")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load configuration", e)
            }
        }
    }

    override fun getValue(key: String, profileId: String?): Flow<String?> {
        return dataStore.data.map { prefs ->
            val profile = profileId ?: _currentProfile.value?.id ?: "default"
            val fullKey = if (profile == "default") {
                stringPreferencesKey("${KEY_PREFIX}${key}")
            } else {
                stringPreferencesKey("${KEY_PREFIX}${profile}_${key}")
            }
            prefs[fullKey] ?: configCache[key]?.value
        }
    }

    override suspend fun getValueSync(key: String, profileId: String?): String? {
        return try {
            val profile = profileId ?: _currentProfile.value?.id ?: "default"
            val fullKey = if (profile == "default") {
                stringPreferencesKey("${KEY_PREFIX}${key}")
            } else {
                stringPreferencesKey("${KEY_PREFIX}${profile}_${key}")
            }
            val prefs = dataStore.data.first()
            prefs[fullKey] ?: configCache[key]?.value
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get value for $key", e)
            null
        }
    }

    override suspend fun updateValue(key: String, value: String, profileId: String?, scope: ConfigurationScope): Result<Unit> {
        return try {
            if (key.isBlank()) return Result.failure(IllegalArgumentException("Key cannot be blank"))
            val profile = profileId ?: _currentProfile.value?.id ?: "default"
            val fullKey = if (profile == "default") {
                stringPreferencesKey("${KEY_PREFIX}${key}")
            } else {
                stringPreferencesKey("${KEY_PREFIX}${profile}_${key}")
            }

            dataStore.edit { prefs ->
                prefs[fullKey] = value
            }

            configCache[key] = ConfigurationValue(key = key, value = value, profileId = profile, scope = scope)

            _onDidChangeConfiguration.fire(
                ConfigurationChangeEvent(
                    keys = listOf(key),
                    profileId = profile,
                    scope = scope
                )
            )

            Log.i(TAG, "Updated config $key=$value for profile $profile scope $scope")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update config $key", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteValue(key: String, profileId: String?): Result<Unit> {
        return try {
            if (key.isBlank()) return Result.failure(IllegalArgumentException("Key cannot be blank"))
            val profile = profileId ?: _currentProfile.value?.id ?: "default"
            val fullKey = if (profile == "default") {
                stringPreferencesKey("${KEY_PREFIX}${key}")
            } else {
                stringPreferencesKey("${KEY_PREFIX}${profile}_${key}")
            }

            dataStore.edit { prefs ->
                prefs.remove(fullKey)
            }

            configCache.remove(key)

            _onDidChangeConfiguration.fire(
                ConfigurationChangeEvent(
                    keys = listOf(key),
                    profileId = profile,
                    scope = ConfigurationScope.USER
                )
            )

            Log.i(TAG, "Deleted config $key for profile $profile")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete config $key", e)
            Result.failure(e)
        }
    }

    override suspend fun createProfile(name: String): Result<ConfigurationProfile> {
        return try {
            if (name.isBlank()) return Result.failure(IllegalArgumentException("Profile name cannot be blank"))
            if (_profiles.value.any { it.name.equals(name, ignoreCase = true) }) {
                return Result.failure(IllegalArgumentException("Profile with name $name already exists"))
            }

            val profile = ConfigurationProfile(
                id = UUID.randomUUID().toString(),
                name = name,
                isDefault = false
            )

            val updated = _profiles.value + profile
            _profiles.value = updated

            // Persist profiles
            dataStore.edit { prefs ->
                val profilesSet = updated.map { p ->
                    "${p.id}:${p.name}:${p.isDefault}:${p.createdAt}:${p.lastUsedAt}"
                }.toSet()
                prefs[KEY_PROFILES] = profilesSet
            }

            Log.i(TAG, "Created profile ${profile.name} with id ${profile.id}")
            Result.success(profile)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create profile $name", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(profileId: String): Result<Unit> {
        return try {
            if (profileId == "default") return Result.failure(IllegalArgumentException("Cannot delete default profile"))
            val profile = _profiles.value.find { it.id == profileId }
                ?: return Result.failure(IllegalArgumentException("Profile $profileId not found"))

            val updated = _profiles.value.filter { it.id != profileId }
            _profiles.value = updated

            if (_currentProfile.value?.id == profileId) {
                _currentProfile.value = updated.firstOrNull()
                dataStore.edit { prefs ->
                    prefs[KEY_CURRENT_PROFILE] = _currentProfile.value?.id ?: "default"
                }
            }

            dataStore.edit { prefs ->
                val profilesSet = updated.map { p ->
                    "${p.id}:${p.name}:${p.isDefault}:${p.createdAt}:${p.lastUsedAt}"
                }.toSet()
                prefs[KEY_PROFILES] = profilesSet
            }

            Log.i(TAG, "Deleted profile ${profile.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete profile $profileId", e)
            Result.failure(e)
        }
    }

    override suspend fun renameProfile(profileId: String, newName: String): Result<Unit> {
        return try {
            if (newName.isBlank()) return Result.failure(IllegalArgumentException("New name cannot be blank"))
            if (profileId == "default") return Result.failure(IllegalArgumentException("Cannot rename default profile"))
            val profile = _profiles.value.find { it.id == profileId }
                ?: return Result.failure(IllegalArgumentException("Profile $profileId not found"))
            if (_profiles.value.any { it.name.equals(newName, ignoreCase = true) && it.id != profileId }) {
                return Result.failure(IllegalArgumentException("Profile with name $newName already exists"))
            }

            val updatedProfile = profile.copy(name = newName)
            val updated = _profiles.value.map { if (it.id == profileId) updatedProfile else it }
            _profiles.value = updated

            if (_currentProfile.value?.id == profileId) {
                _currentProfile.value = updatedProfile
            }

            dataStore.edit { prefs ->
                val profilesSet = updated.map { p ->
                    "${p.id}:${p.name}:${p.isDefault}:${p.createdAt}:${p.lastUsedAt}"
                }.toSet()
                prefs[KEY_PROFILES] = profilesSet
            }

            Log.i(TAG, "Renamed profile $profileId to $newName")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to rename profile $profileId", e)
            Result.failure(e)
        }
    }

    override suspend fun switchProfile(profileId: String): Result<Unit> {
        return try {
            val profile = _profiles.value.find { it.id == profileId }
                ?: return Result.failure(IllegalArgumentException("Profile $profileId not found"))

            val updatedProfile = profile.copy(lastUsedAt = System.currentTimeMillis())
            val updated = _profiles.value.map { if (it.id == profileId) updatedProfile else it }
            _profiles.value = updated
            _currentProfile.value = updatedProfile

            dataStore.edit { prefs ->
                prefs[KEY_CURRENT_PROFILE] = profileId
                val profilesSet = updated.map { p ->
                    "${p.id}:${p.name}:${p.isDefault}:${p.createdAt}:${p.lastUsedAt}"
                }.toSet()
                prefs[KEY_PROFILES] = profilesSet
            }

            _onDidChangeConfiguration.fire(
                ConfigurationChangeEvent(
                    keys = listOf("*"),
                    profileId = profileId,
                    scope = ConfigurationScope.USER
                )
            )

            Log.i(TAG, "Switched to profile ${profile.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to switch to profile $profileId", e)
            Result.failure(e)
        }
    }

    override fun getProfile(profileId: String): ConfigurationProfile? {
        return _profiles.value.find { it.id == profileId }
    }

    override fun inspect(key: String): ConfigurationValue? {
        return configCache[key]
    }

    override fun keys(): List<String> {
        return configCache.keys.toList()
    }

    override fun dispose() {
        super.dispose()
        _onDidChangeConfiguration.dispose()
        Log.i(TAG, "Disposed")
    }
}
