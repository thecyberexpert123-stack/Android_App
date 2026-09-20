package com.cyberexpert.androde.core.search

import com.cyberexpert.androde.domain.model.ide.SearchQuery
import com.cyberexpert.androde.domain.model.ide.SearchResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface SearchRepository {
    fun search(query: SearchQuery, root: File): Flow<SearchProgress>
    suspend fun replace(query: SearchQuery, replacement: String, root: File): Result<Int>
}

sealed class SearchProgress {
    data class Started(val query: String) : SearchProgress()
    data class FileSearched(val file: File, val matchesInFile: Int, val totalFiles: Int) : SearchProgress()
    data class ResultFound(val result: SearchResult) : SearchProgress()
    data class Completed(val totalMatches: Int, val totalFiles: Int) : SearchProgress()
    data class Error(val message: String) : SearchProgress()
}
