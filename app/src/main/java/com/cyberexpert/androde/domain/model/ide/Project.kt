package com.cyberexpert.androde.domain.model.ide

import java.io.File

/**
 * Represents a workspace/project opened in Androde.
 * Mirrors VS Code workspace concept.
 */
data class Project(
    val id: String,
    val name: String,
    val rootPath: String,
    val rootFile: File,
    val isGitRepository: Boolean = false,
    val lastOpened: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
) {
    val displayName: String get() = name.ifBlank { rootFile.name }

    companion object {
        fun fromFile(file: File): Project {
            return Project(
                id = file.absolutePath.hashCode().toString(),
                name = file.name,
                rootPath = file.absolutePath,
                rootFile = file,
                isGitRepository = File(file, ".git").exists()
            )
        }
    }
}
