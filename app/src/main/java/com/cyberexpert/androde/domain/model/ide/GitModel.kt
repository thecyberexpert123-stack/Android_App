package com.cyberexpert.androde.domain.model.ide

import java.io.File

/**
 * Git models mirroring VS Code Source Control view.
 */
data class GitRepository(
    val rootFile: File,
    val rootPath: String = rootFile.absolutePath,
    val currentBranch: String = "main",
    val branches: List<GitBranch> = emptyList(),
    val status: GitStatus = GitStatus(),
    val remotes: List<GitRemote> = emptyList(),
    val lastCommit: GitCommit? = null
)

data class GitStatus(
    val staged: List<GitFile> = emptyList(),
    val unstaged: List<GitFile> = emptyList(),
    val untracked: List<GitFile> = emptyList(),
    val conflicted: List<GitFile> = emptyList(),
    val isClean: Boolean = staged.isEmpty() && unstaged.isEmpty() && untracked.isEmpty() && conflicted.isEmpty()
)

data class GitFile(
    val file: File,
    val path: String = file.absolutePath,
    val relativePath: String,
    val status: GitFileStatus,
    val staged: Boolean = false
)

enum class GitFileStatus {
    ADDED, MODIFIED, DELETED, RENAMED, UNTRACKED, CONFLICTED, IGNORED
}

data class GitBranch(
    val name: String,
    val isCurrent: Boolean = false,
    val isRemote: Boolean = false,
    val commitId: String = "",
    val commitMessage: String = ""
)

data class GitCommit(
    val id: String,
    val shortId: String = id.take(7),
    val message: String,
    val author: String,
    val email: String,
    val timestamp: Long,
    val parents: List<String> = emptyList()
)

data class GitRemote(
    val name: String,
    val url: String,
    val fetchUrl: String = url,
    val pushUrl: String = url
)
