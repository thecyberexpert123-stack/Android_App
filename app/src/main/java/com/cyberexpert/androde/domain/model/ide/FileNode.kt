package com.cyberexpert.androde.domain.model.ide

import java.io.File

/**
 * Represents a file or folder in the file explorer.
 * Mirrors VS Code Explorer tree node.
 */
data class FileNode(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val isDirectory: Boolean = file.isDirectory,
    val isHidden: Boolean = file.isHidden || name.startsWith("."),
    val extension: String = if (file.isFile) file.extension.lowercase() else "",
    val size: Long = if (file.isFile) file.length() else 0L,
    val lastModified: Long = file.lastModified(),
    val children: List<FileNode> = emptyList(),
    val depth: Int = 0,
    val isExpanded: Boolean = false,
    val gitStatus: GitFileStatus? = null
) {
    val isFile: Boolean get() = !isDirectory

    enum class GitFileStatus {
        UNTRACKED, MODIFIED, ADDED, DELETED, RENAMED, CONFLICTED, IGNORED, CLEAN
    }

    companion object {
        fun fromFile(file: File, depth: Int = 0, showHidden: Boolean = false): FileNode? {
            if (!showHidden && file.isHidden) return null
            if (!showHidden && file.name.startsWith(".")) return null
            return FileNode(
                file = file,
                depth = depth
            )
        }
    }
}

/**
 * Flat representation for LazyColumn with indentation.
 */
data class FlatFileNode(
    val node: FileNode,
    val depth: Int
)
