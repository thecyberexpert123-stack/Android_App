package com.cyberexpert.androde.data.local.file

import android.util.Log
import com.cyberexpert.androde.core.search.SearchProgress
import com.cyberexpert.androde.core.search.SearchRepository
import com.cyberexpert.androde.domain.model.ide.SearchQuery
import com.cyberexpert.androde.domain.model.ide.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Search implementation similar to VS Code's ripgrep-based search - Phase 5 with real replace.
 * Uses Kotlin coroutines for background search, supports regex, case sensitivity, replace.
 * Optimized for Android: chunked reading, max file size check, binary skip.
 * Production-ready with error handling, Flow, progress.
 */
@Singleton
class SearchRepositoryImpl @Inject constructor() : SearchRepository {

    override fun search(query: SearchQuery, root: File): Flow<SearchProgress> = flow {
        if (query.query.isBlank()) {
            emit(SearchProgress.Error("Query cannot be empty"))
            return@flow
        }

        emit(SearchProgress.Started(query.query))

        val regex = if (query.isRegex) {
            try {
                val flags = if (query.isCaseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE)
                Regex(query.query, flags)
            } catch (e: Exception) {
                emit(SearchProgress.Error("Invalid regex: ${e.message}"))
                return@flow
            }
        } else {
            null
        }

        var totalMatches = 0
        var totalFiles = 0
        val filesToSearch = mutableListOf<File>()

        fun collectFiles(dir: File) {
            dir.listFiles()?.forEach { file ->
                if (file.name.startsWith(".") && !query.includePattern.contains(".")) return@forEach
                if (file.isDirectory) {
                    if (file.name == "node_modules" || file.name == ".git" || file.name == "build" || file.name == ".gradle") return@forEach
                    collectFiles(file)
                } else {
                    if (query.filesToInclude.isNotEmpty() && query.filesToInclude.none { file.name.contains(it) }) return@forEach
                    if (query.filesToExclude.any { file.name.contains(it) }) return@forEach
                    if (file.length() > 5 * 1024 * 1024) return@forEach
                    if (isBinary(file)) return@forEach
                    filesToSearch.add(file)
                }
            }
        }

        collectFiles(root)

        for (file in filesToSearch) {
            totalFiles++
            try {
                var matchesInFile = 0
                file.useLines { lines ->
                    lines.forEachIndexed { index, line ->
                        val matches = if (regex != null) {
                            regex.findAll(line).toList()
                        } else {
                            val searchIn = if (query.isCaseSensitive) line else line.lowercase()
                            val q = if (query.isCaseSensitive) query.query else query.query.lowercase()
                            val idx = searchIn.indexOf(q)
                            if (idx >= 0) listOf(RegexMatch(idx, idx + q.length)) else emptyList()
                        }
                        matches.forEach { m ->
                            val (s, e) = when (m) {
                                is RegexMatch -> m.start to m.end
                                is MatchResult -> m.range.first to m.range.last + 1
                                else -> 0 to 0
                            }
                            totalMatches++
                            matchesInFile++
                            emit(
                                SearchProgress.ResultFound(
                                    SearchResult(
                                        file = file,
                                        lineNumber = index + 1,
                                        column = s + 1,
                                        lineContent = line,
                                        matchStart = s,
                                        matchEnd = e
                                    )
                                )
                            )
                        }
                    }
                }
                emit(SearchProgress.FileSearched(file, matchesInFile, totalFiles))
            } catch (e: Exception) {
                Log.w("SearchRepo", "Failed to search file ${file.path}", e)
            }
        }

        emit(SearchProgress.Completed(totalMatches, totalFiles))
    }

    override suspend fun replace(query: SearchQuery, replacement: String, root: File): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                var replaced = 0
                val regex = if (query.isRegex) {
                    try {
                        val flags = if (query.isCaseSensitive) emptySet() else setOf(RegexOption.IGNORE_CASE)
                        Regex(query.query, flags)
                    } catch (e: Exception) {
                        return@withContext Result.failure(IllegalArgumentException("Invalid regex: ${e.message}"))
                    }
                } else {
                    null
                }

                val filesToSearch = mutableListOf<File>()
                fun collectFiles(dir: File) {
                    dir.listFiles()?.forEach { file ->
                        if (file.isDirectory) {
                            if (file.name == "node_modules" || file.name == ".git" || file.name == "build" || file.name == ".gradle") return@forEach
                            collectFiles(file)
                        } else {
                            if (file.length() > 5 * 1024 * 1024) return@forEach
                            if (isBinary(file)) return@forEach
                            filesToSearch.add(file)
                        }
                    }
                }
                collectFiles(root)

                for (file in filesToSearch) {
                    try {
                        val original = file.readText()
                        val newContent = if (regex != null) {
                            regex.replace(original, replacement)
                        } else {
                            if (query.isCaseSensitive) {
                                original.replace(query.query, replacement)
                            } else {
                                // Case-insensitive replace
                                original.replace(Regex(Regex.escape(query.query), setOf(RegexOption.IGNORE_CASE)), replacement)
                            }
                        }
                        if (newContent != original) {
                            file.writeText(newContent)
                            replaced++
                            Log.i("SearchRepo", "Replaced in ${file.path}")
                        }
                    } catch (e: Exception) {
                        Log.w("SearchRepo", "Failed to replace in ${file.path}", e)
                    }
                }

                Log.i("SearchRepo", "Replace completed: $replaced files")
                Result.success(replaced)
            } catch (e: Exception) {
                Log.e("SearchRepo", "Replace failed", e)
                Result.failure(e)
            }
        }

    private fun isBinary(file: File): Boolean {
        return try {
            val bytes = file.inputStream().use { it.readNBytes(1024) }
            bytes.contains(0.toByte())
        } catch (e: Exception) {
            true
        }
    }

    private data class RegexMatch(val start: Int, val end: Int)
}
