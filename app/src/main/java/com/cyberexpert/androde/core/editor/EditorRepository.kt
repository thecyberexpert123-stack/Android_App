package com.cyberexpert.androde.core.editor

import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.EditorTab
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * Editor repository for managing open tabs, similar to VS Code editor groups.
 */
interface EditorRepository {
    fun getOpenTabs(): Flow<List<EditorTab>>
    fun getActiveTab(): Flow<EditorTab?>
    suspend fun openFile(file: File): AppResult<EditorTab>
    suspend fun openUntitled(content: String = "", language: String = "plaintext"): AppResult<EditorTab>
    suspend fun closeTab(tabId: String): AppResult<Unit>
    suspend fun closeAllTabs(): AppResult<Unit>
    suspend fun saveTab(tabId: String): AppResult<Unit>
    suspend fun saveAllTabs(): AppResult<Unit>
    suspend fun updateTabContent(tabId: String, content: String): AppResult<Unit>
    suspend fun setActiveTab(tabId: String): AppResult<Unit>
    suspend fun pinTab(tabId: String, pinned: Boolean): AppResult<Unit>
}
