package com.cyberexpert.androde.domain.model.ide

import java.io.File

/**
 * Represents a search result, similar to VS Code Search view.
 */
data class SearchResult(
    val file: File,
    val filePath: String = file.absolutePath,
    val fileName: String = file.name,
    val lineNumber: Int,
    val column: Int,
    val lineContent: String,
    val matchStart: Int,
    val matchEnd: Int,
    val preview: String = lineContent.trim()
)

data class SearchQuery(
    val query: String,
    val isRegex: Boolean = false,
    val isCaseSensitive: Boolean = false,
    val isWholeWord: Boolean = false,
    val includePattern: String = "",
    val excludePattern: String = "",
    val filesToInclude: List<String> = emptyList(),
    val filesToExclude: List<String> = emptyList()
)

data class SearchState(
    val query: SearchQuery = SearchQuery(""),
    val results: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val totalFilesSearched: Int = 0,
    val totalMatches: Int = 0
)
