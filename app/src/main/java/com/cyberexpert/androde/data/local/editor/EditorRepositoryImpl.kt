package com.cyberexpert.androde.data.local.editor

import com.cyberexpert.androde.core.editor.EditorRepository
import com.cyberexpert.androde.core.error.AppError
import com.cyberexpert.androde.core.filesystem.FileSystemRepository
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.EditorLanguage
import com.cyberexpert.androde.domain.model.ide.EditorTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production implementation of EditorRepository.
 * Manages tabs in memory, persists content via FileSystemRepository.
 * Similar to VS Code's editor group management.
 */
@Singleton
class EditorRepositoryImpl @Inject constructor(
    private val fileSystemRepository: FileSystemRepository
) : EditorRepository {

    private val _openTabs = MutableStateFlow<List<EditorTab>>(emptyList())
    private val _activeTab = MutableStateFlow<EditorTab?>(null)

    override fun getOpenTabs(): Flow<List<EditorTab>> = _openTabs.asStateFlow()
    override fun getActiveTab(): Flow<EditorTab?> = _activeTab.asStateFlow()

    override suspend fun openFile(file: File): AppResult<EditorTab> {
        try {
            // Check if already open
            val existing = _openTabs.value.find { it.filePath == file.absolutePath }
            if (existing != null) {
                _activeTab.value = existing
                return AppResult.Success(existing)
            }

            val contentResult = fileSystemRepository.readFile(file)
            if (contentResult is AppResult.Error) {
                return contentResult
            }
            val content = (contentResult as AppResult.Success).data

            val tab = EditorTab(
                id = UUID.randomUUID().toString(),
                file = file,
                content = content,
                originalContent = content,
                language = EditorLanguage.fromExtension(file.extension)
            )

            _openTabs.value = _openTabs.value + tab
            _activeTab.value = tab
            return AppResult.Success(tab)
        } catch (e: Exception) {
            return AppResult.Error(AppError.Local("Failed to open file: ${e.message}", e))
        }
    }

    override suspend fun openUntitled(content: String, language: String): AppResult<EditorTab> {
        return try {
            val file = File("untitled:${UUID.randomUUID()}")
            val tab = EditorTab(
                id = UUID.randomUUID().toString(),
                file = file,
                fileName = "Untitled-${_openTabs.value.size + 1}",
                filePath = "untitled:${UUID.randomUUID()}",
                content = content,
                originalContent = "",
                language = EditorLanguage.entries.find { it.id == language } ?: EditorLanguage.PLAINTEXT,
                isDirty = content.isNotEmpty()
            )
            _openTabs.value = _openTabs.value + tab
            _activeTab.value = tab
            AppResult.Success(tab)
        } catch (e: Exception) {
            AppResult.Error(AppError.Local("Failed to create untitled file", e))
        }
    }

    override suspend fun closeTab(tabId: String): AppResult<Unit> {
        _openTabs.value = _openTabs.value.filter { it.id != tabId }
        if (_activeTab.value?.id == tabId) {
            _activeTab.value = _openTabs.value.lastOrNull()
        }
        return AppResult.Success(Unit)
    }

    override suspend fun closeAllTabs(): AppResult<Unit> {
        _openTabs.value = emptyList()
        _activeTab.value = null
        return AppResult.Success(Unit)
    }

    override suspend fun saveTab(tabId: String): AppResult<Unit> {
        val tab = _openTabs.value.find { it.id == tabId } ?: return AppResult.Error(AppError.Local("Tab not found"))
        if (tab.isUntitled) return AppResult.Error(AppError.Local("Cannot save untitled file without path"))
        val result = fileSystemRepository.writeFile(tab.file, tab.content)
        if (result is AppResult.Success) {
            val updated = tab.asSaved()
            _openTabs.value = _openTabs.value.map { if (it.id == tabId) updated else it }
            if (_activeTab.value?.id == tabId) _activeTab.value = updated
        }
        return result
    }

    override suspend fun saveAllTabs(): AppResult<Unit> {
        _openTabs.value.forEach { tab ->
            if (tab.isDirty && !tab.isUntitled) {
                fileSystemRepository.writeFile(tab.file, tab.content)
            }
        }
        _openTabs.value = _openTabs.value.map { if (!it.isUntitled) it.asSaved() else it }
        return AppResult.Success(Unit)
    }

    override suspend fun updateTabContent(tabId: String, content: String): AppResult<Unit> {
        _openTabs.value = _openTabs.value.map { tab ->
            if (tab.id == tabId) tab.withContent(content) else tab
        }
        if (_activeTab.value?.id == tabId) {
            _activeTab.value = _activeTab.value?.withContent(content)
        }
        return AppResult.Success(Unit)
    }

    override suspend fun setActiveTab(tabId: String): AppResult<Unit> {
        val tab = _openTabs.value.find { it.id == tabId } ?: return AppResult.Error(AppError.Local("Tab not found"))
        _activeTab.value = tab
        return AppResult.Success(Unit)
    }

    override suspend fun pinTab(tabId: String, pinned: Boolean): AppResult<Unit> {
        _openTabs.value = _openTabs.value.map { if (it.id == tabId) it.copy(isPinned = pinned) else it }
        return AppResult.Success(Unit)
    }
}
