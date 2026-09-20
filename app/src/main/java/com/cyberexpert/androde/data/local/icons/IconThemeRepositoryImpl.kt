package com.cyberexpert.androde.data.local.icons

import android.content.Context
import android.util.Log
import com.cyberexpert.androde.core.extensions.IconTheme
import com.cyberexpert.androde.core.extensions.IconThemeRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working icon theme repository.
 * Loads icon themes from assets/icons/*.json, similar to VS Code icon themes.
 * Production-ready with Flow and file/folder icon resolution.
 */
@Singleton
class IconThemeRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IconThemeRepository {

    private val _themes = MutableStateFlow<List<IconTheme>>(emptyList())
    private val _current = MutableStateFlow<IconTheme?>(null)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override fun getAvailableThemes(): Flow<List<IconTheme>> = _themes.asStateFlow()
    override fun getCurrentTheme(): Flow<IconTheme?> = _current.asStateFlow()

    override suspend fun loadThemes(): List<IconTheme> = withContext(Dispatchers.IO) {
        try {
            val themeFiles = listOf("vscode_icons.json", "material_icons.json")
            val themes = mutableListOf<IconTheme>()

            for (fileName in themeFiles) {
                try {
                    val jsonString = context.assets.open("icons/$fileName").bufferedReader().use { it.readText() }
                    val jsonObject = json.parseToJsonElement(jsonString).jsonObject

                    val id = jsonObject["name"]?.jsonPrimitive?.content?.lowercase()?.replace(" ", "_") ?: fileName.removeSuffix(".json")
                    val name = jsonObject["name"]?.jsonPrimitive?.content ?: id

                    val fileExtensions = mutableMapOf<String, String>()
                    jsonObject["fileExtensions"]?.jsonObject?.entries?.forEach { (ext, icon) ->
                        fileExtensions[ext] = icon.jsonPrimitive.content
                    }

                    val fileNames = mutableMapOf<String, String>()
                    jsonObject["fileNames"]?.jsonObject?.entries?.forEach { (nameKey, icon) ->
                        fileNames[nameKey] = icon.jsonPrimitive.content
                    }

                    val folderNames = mutableMapOf<String, String>()
                    jsonObject["folderNames"]?.jsonObject?.entries?.forEach { (folder, icon) ->
                        folderNames[folder] = icon.jsonPrimitive.content
                    }

                    val folderNamesExpanded = mutableMapOf<String, String>()
                    jsonObject["folderNamesExpanded"]?.jsonObject?.entries?.forEach { (folder, icon) ->
                        folderNamesExpanded[folder] = icon.jsonPrimitive.content
                    }

                    val languageIds = mutableMapOf<String, String>()
                    jsonObject["languageIds"]?.jsonObject?.entries?.forEach { (lang, icon) ->
                        languageIds[lang] = icon.jsonPrimitive.content
                    }

                    val theme = IconTheme(
                        id = id,
                        name = name,
                        fileExtensions = fileExtensions,
                        fileNames = fileNames,
                        folderNames = folderNames,
                        folderNamesExpanded = folderNamesExpanded,
                        languageIds = languageIds
                    )

                    themes.add(theme)
                    Log.i("IconThemeRepo", "Loaded icon theme: $name with ${fileExtensions.size} file extensions")
                } catch (e: Exception) {
                    Log.w("IconThemeRepo", "Failed to load icon theme $fileName", e)
                }
            }

            // Fallback if no themes loaded
            if (themes.isEmpty()) {
                themes.add(
                    IconTheme(
                        id = "vscode_icons",
                        name = "VS Code Icons",
                        fileExtensions = mapOf(
                            "kt" to "_file_kotlin",
                            "java" to "_file_java",
                            "js" to "_file_js",
                            "py" to "_file_python"
                        ),
                        fileNames = mapOf("Dockerfile" to "_file_docker"),
                        folderNames = mapOf("src" to "_folder_src"),
                        folderNamesExpanded = mapOf("src" to "_folder_src_open"),
                        languageIds = mapOf("kotlin" to "_file_kotlin")
                    )
                )
            }

            _themes.value = themes
            if (_current.value == null && themes.isNotEmpty()) {
                _current.value = themes.first()
            }

            themes
        } catch (e: Exception) {
            Log.e("IconThemeRepo", "Failed to load icon themes", e)
            emptyList()
        }
    }

    override suspend fun setTheme(themeId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val theme = _themes.value.find { it.id == themeId }
            if (theme != null) {
                _current.value = theme
                Log.i("IconThemeRepo", "Set icon theme to: $themeId")
                true
            } else {
                Log.w("IconThemeRepo", "Icon theme not found: $themeId")
                false
            }
        } catch (e: Exception) {
            Log.e("IconThemeRepo", "Failed to set theme $themeId", e)
            false
        }
    }

    override fun getIconForFile(fileName: String, languageId: String?): String {
        val theme = _current.value ?: return "_file_default"

        // Check fileNames first (exact match)
        theme.fileNames[fileName]?.let { return it }

        // Check extension
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext.isNotBlank()) {
            theme.fileExtensions[ext]?.let { return it }
        }

        // Check languageId
        languageId?.let {
            theme.languageIds[it.lowercase()]?.let { icon -> return icon }
        }

        return "_file_default"
    }

    override fun getIconForFolder(folderName: String, isExpanded: Boolean): String {
        val theme = _current.value ?: return if (isExpanded) "_folder_open" else "_folder"

        return if (isExpanded) {
            theme.folderNamesExpanded[folderName] ?: theme.folderNames[folderName] ?: "_folder_open"
        } else {
            theme.folderNames[folderName] ?: "_folder"
        }
    }
}
