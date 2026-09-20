package com.cyberexpert.androde.data.local.snippets

import android.content.Context
import android.util.Log
import com.cyberexpert.androde.core.extensions.Snippet
import com.cyberexpert.androde.core.extensions.SnippetRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonArray
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Real working snippet repository.
 * Loads snippets from assets/snippets/*.json, similar to VS Code snippets.
 * Production-ready with error handling, Flow, search.
 */
@Singleton
class SnippetRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SnippetRepository {

    private val _allSnippets = MutableStateFlow<Map<String, List<Snippet>>>(emptyMap())
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override fun getSnippetsForLanguage(languageId: String): Flow<List<Snippet>> {
        return _allSnippets.map { it[languageId.lowercase()] ?: emptyList() }
    }

    override suspend fun loadSnippets(): Map<String, List<Snippet>> = withContext(Dispatchers.IO) {
        try {
            val languages = listOf("kotlin", "java", "javascript", "python", "html", "toml", "groovy", "lua", "shell", "yaml")
            val result = mutableMapOf<String, List<Snippet>>()

            for (lang in languages) {
                try {
                    val jsonString = context.assets.open("snippets/$lang.json").bufferedReader().use { it.readText() }
                    val jsonObject = json.parseToJsonElement(jsonString).jsonObject

                    val snippets = jsonObject.entries.mapNotNull { (name, element) ->
                        try {
                            val obj = element.jsonObject
                            val prefix = obj["prefix"]?.jsonPrimitive?.content ?: return@mapNotNull null
                            val bodyElement = obj["body"]
                            val body = when {
                                bodyElement == null -> emptyList()
                                bodyElement is kotlinx.serialization.json.JsonArray -> bodyElement.map { it.jsonPrimitive.content }
                                else -> listOf(bodyElement.jsonPrimitive.content)
                            }
                            val description = obj["description"]?.jsonPrimitive?.content ?: ""

                            Snippet(
                                prefix = prefix,
                                body = body,
                                description = description,
                                language = lang,
                                name = name
                            )
                        } catch (e: Exception) {
                            Log.w("SnippetRepo", "Failed to parse snippet $name for $lang", e)
                            null
                        }
                    }

                    result[lang] = snippets
                    Log.i("SnippetRepo", "Loaded ${snippets.size} snippets for $lang")
                } catch (e: Exception) {
                    Log.w("SnippetRepo", "No snippets for $lang", e)
                    result[lang] = emptyList()
                }
            }

            // Also add generic snippets for all languages
            _allSnippets.value = result
            result
        } catch (e: Exception) {
            Log.e("SnippetRepo", "Failed to load snippets", e)
            emptyMap()
        }
    }

    override suspend fun searchSnippets(query: String, languageId: String?): List<Snippet> = withContext(Dispatchers.IO) {
        if (_allSnippets.value.isEmpty()) {
            loadSnippets()
        }

        val all = if (languageId != null) {
            _allSnippets.value[languageId.lowercase()] ?: emptyList()
        } else {
            _allSnippets.value.values.flatten()
        }

        if (query.isBlank()) return@withContext all

        all.filter { snippet ->
            snippet.prefix.contains(query, ignoreCase = true) ||
                    snippet.name.contains(query, ignoreCase = true) ||
                    snippet.description.contains(query, ignoreCase = true)
        }
    }
}
