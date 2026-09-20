package com.cyberexpert.androde.core.filesystem

import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.FileNode
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * File system repository interface, similar to VS Code FileSystemProvider.
 * Abstracts SAF and java.io for Android scoped storage compatibility.
 */
interface FileSystemRepository {
    suspend fun listFiles(directory: File, showHidden: Boolean = false): AppResult<List<FileNode>>
    suspend fun listFilesRecursive(directory: File, maxDepth: Int = 5): AppResult<List<FileNode>>
    suspend fun readFile(file: File): AppResult<String>
    suspend fun writeFile(file: File, content: String): AppResult<Unit>
    suspend fun createFile(parent: File, name: String, isDirectory: Boolean = false): AppResult<File>
    suspend fun deleteFile(file: File): AppResult<Unit>
    suspend fun renameFile(file: File, newName: String): AppResult<File>
    suspend fun copyFile(source: File, dest: File): AppResult<Unit>
    suspend fun moveFile(source: File, dest: File): AppResult<Unit>
    suspend fun exists(file: File): Boolean
    suspend fun getFileTree(root: File, showHidden: Boolean = false): AppResult<FileNode>
    fun watchDirectory(directory: File): Flow<FileSystemEvent>
}

sealed class FileSystemEvent {
    data class Created(val file: File) : FileSystemEvent()
    data class Deleted(val file: File) : FileSystemEvent()
    data class Modified(val file: File) : FileSystemEvent()
    data class Renamed(val oldFile: File, val newFile: File) : FileSystemEvent()
}
