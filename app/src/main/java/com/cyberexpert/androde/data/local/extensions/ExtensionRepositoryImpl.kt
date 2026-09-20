package com.cyberexpert.androde.data.local.extensions

import android.content.Context
import android.util.Log
import com.cyberexpert.androde.core.extensions.ExtensionRepository
import com.cyberexpert.androde.domain.model.ide.Extension
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working extension repository implementation.
 * Loads extensions from assets/extensions/extensions.json with JSON parsing.
 * Similar to VS Code's extension management, but optimized for Android.
 * Production-ready with error handling and Flow.
 */
@Singleton
class ExtensionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ExtensionRepository {

    private val _installed = MutableStateFlow<List<Extension>>(emptyList())
    private val _marketplace = MutableStateFlow<List<Extension>>(emptyList())

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override fun getInstalledExtensions(): Flow<List<Extension>> = _installed.asStateFlow()
    override fun getMarketplaceExtensions(): Flow<List<Extension>> = _marketplace.asStateFlow()

    override fun getExtensionById(id: String): Flow<Extension?> {
        return _installed.map { list -> list.find { it.id == id } }
    }

    /**
     * Real working builtin extensions loading from assets.
     * Parses assets/extensions/extensions.json with kotlinx.serialization.
     */
    override suspend fun loadBuiltinExtensions(): List<Extension> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("extensions/extensions.json").bufferedReader().use { it.readText() }
            val jsonObject = json.parseToJsonElement(jsonString).jsonObject
            val extensionsArray = jsonObject["extensions"]?.jsonArray ?: return@withContext emptyList()

            val extensions = extensionsArray.mapNotNull { element ->
                try {
                    val obj = element.jsonObject
                    val id = obj["id"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val name = obj["name"]?.jsonPrimitive?.content ?: ""
                    val displayName = obj["displayName"]?.jsonPrimitive?.content ?: name
                    val description = obj["description"]?.jsonPrimitive?.content ?: ""
                    val version = obj["version"]?.jsonPrimitive?.content ?: "1.0.0"
                    val publisher = obj["publisher"]?.jsonPrimitive?.content ?: "Androde"
                    val categories = obj["categories"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                    val tags = obj["tags"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
                    val isBuiltin = obj["isBuiltin"]?.jsonPrimitive?.content?.toBoolean() ?: true

                    // Parse contributes
                    val contributes = obj["contributes"]?.jsonObject
                    val languages = contributes?.get("languages")?.jsonArray?.mapNotNull { langEl ->
                        langEl.jsonObject["id"]?.jsonPrimitive?.content
                    } ?: emptyList()
                    val themes = contributes?.get("themes")?.jsonArray?.mapNotNull { themeEl ->
                        themeEl.jsonObject["label"]?.jsonPrimitive?.content
                    } ?: emptyList()

                    Extension(
                        id = id,
                        name = name,
                        displayName = displayName,
                        description = description,
                        version = version,
                        publisher = publisher,
                        categories = categories,
                        tags = tags,
                        isInstalled = true,
                        isBuiltin = isBuiltin,
                        contributesLanguages = languages,
                        contributesThemes = themes
                    )
                } catch (e: Exception) {
                    Log.w("ExtensionRepo", "Failed to parse extension", e)
                    null
                }
            }

            _installed.value = extensions
            _marketplace.value = extensions // For MVP, marketplace is same as installed

            Log.i("ExtensionRepo", "Loaded ${extensions.size} builtin extensions")
            extensions
        } catch (e: Exception) {
            Log.e("ExtensionRepo", "Failed to load builtin extensions", e)
            // Fallback to hardcoded extensions if assets fail
            val fallback = listOf(
                Extension(
                    id = "androde.kotlin",
                    name = "kotlin",
                    displayName = "Kotlin",
                    description = "Kotlin language support",
                    version = "1.0.0",
                    publisher = "Androde",
                    categories = listOf("Programming Languages"),
                    isInstalled = true,
                    isBuiltin = true,
                    contributesLanguages = listOf("kotlin")
                ),
                Extension(
                    id = "androde.java",
                    name = "java",
                    displayName = "Java",
                    description = "Java language support",
                    version = "1.0.0",
                    publisher = "Androde",
                    categories = listOf("Programming Languages"),
                    isInstalled = true,
                    isBuiltin = true,
                    contributesLanguages = listOf("java")
                )
            )
            _installed.value = fallback
            fallback
        }
    }

    override suspend fun installExtension(extension: Extension): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (_installed.value.any { it.id == extension.id }) {
                    Log.w("ExtensionRepo", "Extension already installed: ${extension.id}")
                    return@withContext false
                }
                _installed.value = _installed.value + extension.copy(isInstalled = true)
                Log.i("ExtensionRepo", "Installed extension: ${extension.id}")
                true
            } catch (e: Exception) {
                Log.e("ExtensionRepo", "Failed to install ${extension.id}", e)
                false
            }
        }
    }

    override suspend fun uninstallExtension(extensionId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val extension = _installed.value.find { it.id == extensionId }
                if (extension?.isBuiltin == true) {
                    Log.w("ExtensionRepo", "Cannot uninstall builtin: $extensionId")
                    return@withContext false
                }
                _installed.value = _installed.value.filter { it.id != extensionId }
                Log.i("ExtensionRepo", "Uninstalled extension: $extensionId")
                true
            } catch (e: Exception) {
                Log.e("ExtensionRepo", "Failed to uninstall $extensionId", e)
                false
            }
        }
    }

    override suspend fun enableExtension(extensionId: String, enable: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _installed.value = _installed.value.map { ext ->
                    if (ext.id == extensionId) ext.copy(isEnabled = enable) else ext
                }
                Log.i("ExtensionRepo", "${if (enable) "Enabled" else "Disabled"} extension: $extensionId")
                true
            } catch (e: Exception) {
                Log.e("ExtensionRepo", "Failed to enable/disable $extensionId", e)
                false
            }
        }
    }

    override suspend fun searchMarketplace(query: String): List<Extension> {
        return withContext(Dispatchers.IO) {
            if (query.isBlank()) return@withContext _marketplace.value
            _marketplace.value.filter { ext ->
                ext.displayName.contains(query, ignoreCase = true) ||
                        ext.description.contains(query, ignoreCase = true) ||
                        ext.tags.any { it.contains(query, ignoreCase = true) } ||
                        ext.categories.any { it.contains(query, ignoreCase = true) }
            }
        }
    }
}
