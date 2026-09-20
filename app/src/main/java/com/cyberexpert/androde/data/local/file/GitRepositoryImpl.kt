package com.cyberexpert.androde.data.local.file

import com.cyberexpert.androde.core.error.AppError
import com.cyberexpert.androde.core.git.GitRepository
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.GitBranch
import com.cyberexpert.androde.domain.model.ide.GitCommit
import com.cyberexpert.androde.domain.model.ide.GitFile
import com.cyberexpert.androde.domain.model.ide.GitFileStatus
import com.cyberexpert.androde.domain.model.ide.GitStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * JGit-based Git implementation for Android.
 * Uses org.eclipse.jgit:org.eclipse.jgit which is pure Java and works on Android
 * with proper desugaring and packaging excludes.
 * Mirrors VS Code Git extension functionality.
 */
@Singleton
class GitRepositoryImpl @Inject constructor() : GitRepository {

    private fun openJGitRepo(root: File): Repository {
        return FileRepositoryBuilder()
            .setGitDir(File(root, ".git"))
            .readEnvironment()
            .findGitDir()
            .build()
    }

    override suspend fun openRepository(root: File): AppResult<com.cyberexpert.androde.domain.model.ide.GitRepository> =
        withContext(Dispatchers.IO) {
            try {
                val gitDir = File(root, ".git")
                if (!gitDir.exists()) return@withContext AppResult.Error(AppError.Local("Not a git repository: ${root.path}"))
                val repo = openJGitRepo(root)
                val git = Git(repo)
                val branch = repo.branch ?: "main"
                val status = getStatusInternal(git)
                AppResult.Success(
                    com.cyberexpert.androde.domain.model.ide.GitRepository(
                        rootFile = root,
                        currentBranch = branch,
                        status = status
                    )
                )
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Failed to open repo: ${e.message}", e))
            }
        }

    private fun getStatusInternal(git: Git): GitStatus {
        val jgitStatus = git.status().call()
        val root = git.repository.workTree

        fun toGitFiles(files: Set<String>, status: GitFileStatus, staged: Boolean): List<GitFile> {
            return files.map { path ->
                GitFile(
                    file = File(root, path),
                    relativePath = path,
                    status = status,
                    staged = staged
                )
            }
        }

        return GitStatus(
            staged = toGitFiles(jgitStatus.added, GitFileStatus.ADDED, true) +
                    toGitFiles(jgitStatus.changed, GitFileStatus.MODIFIED, true) +
                    toGitFiles(jgitStatus.removed, GitFileStatus.DELETED, true),
            unstaged = toGitFiles(jgitStatus.modified, GitFileStatus.MODIFIED, false) +
                    toGitFiles(jgitStatus.missing, GitFileStatus.DELETED, false),
            untracked = toGitFiles(jgitStatus.untracked, GitFileStatus.UNTRACKED, false),
            conflicted = toGitFiles(jgitStatus.conflicting, GitFileStatus.CONFLICTED, false)
        )
    }

    override suspend fun getStatus(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): AppResult<GitStatus> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                AppResult.Success(getStatusInternal(git))
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Failed to get status: ${e.message}", e))
            }
        }

    override suspend fun getBranches(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): AppResult<List<GitBranch>> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val branches = git.branchList().call().map { ref ->
                    val name = org.eclipse.jgit.lib.Repository.shortenRefName(ref.name)
                    GitBranch(name = name, isCurrent = name == repo.currentBranch)
                }
                AppResult.Success(branches)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Failed to get branches: ${e.message}", e))
            }
        }

    override suspend fun getCommits(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, maxCount: Int): AppResult<List<GitCommit>> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val commits = git.log().setMaxCount(maxCount).call().map { rev ->
                    GitCommit(
                        id = rev.name,
                        message = rev.shortMessage ?: "",
                        author = rev.authorIdent?.name ?: "",
                        email = rev.authorIdent?.emailAddress ?: "",
                        timestamp = rev.commitTime.toLong() * 1000
                    )
                }
                AppResult.Success(commits)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Failed to get commits: ${e.message}", e))
            }
        }

    override suspend fun stageFile(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, file: File): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val relative = repo.rootFile.toURI().relativize(file.toURI()).path
                git.add().addFilepattern(relative).call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Stage failed: ${e.message}", e))
            }
        }

    override suspend fun unstageFile(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, file: File): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val relative = repo.rootFile.toURI().relativize(file.toURI()).path
                git.reset().addPath(relative).call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Unstage failed: ${e.message}", e))
            }
        }

    override suspend fun stageAll(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                git.add().addFilepattern(".").call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Stage all failed: ${e.message}", e))
            }
        }

    override suspend fun commit(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, message: String): AppResult<GitCommit> =
        withContext(Dispatchers.IO) {
            try {
                if (message.isBlank()) return@withContext AppResult.Error(AppError.Validation("Commit message cannot be blank"))
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val rev = git.commit().setMessage(message).call()
                AppResult.Success(
                    GitCommit(
                        id = rev.name,
                        message = message,
                        author = rev.authorIdent?.name ?: "",
                        email = rev.authorIdent?.emailAddress ?: "",
                        timestamp = System.currentTimeMillis()
                    )
                )
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Commit failed: ${e.message}", e))
            }
        }

    override suspend fun push(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                git.push().call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Push failed: ${e.message}", e))
            }
        }

    override suspend fun pull(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                git.pull().call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Pull failed: ${e.message}", e))
            }
        }

    override suspend fun createBranch(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, name: String): AppResult<GitBranch> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                val ref = git.branchCreate().setName(name).call()
                AppResult.Success(GitBranch(name = name, isCurrent = false, commitId = ref.objectId?.name ?: ""))
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Create branch failed: ${e.message}", e))
            }
        }

    override suspend fun checkoutBranch(repo: com.cyberexpert.androde.domain.model.ide.GitRepository, branch: String): AppResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val jgitRepo = openJGitRepo(repo.rootFile)
                val git = Git(jgitRepo)
                git.checkout().setName(branch).call()
                AppResult.Success(Unit)
            } catch (e: Exception) {
                AppResult.Error(AppError.Local("Checkout failed: ${e.message}", e))
            }
        }

    override fun watchStatus(repo: com.cyberexpert.androde.domain.model.ide.GitRepository): Flow<GitStatus> = flow {
        // Polling for MVP, production would use FileObserver on .git folder
        while (true) {
            val result = getStatus(repo)
            if (result is AppResult.Success) emit(result.data)
            kotlinx.coroutines.delay(2000)
        }
    }
}
