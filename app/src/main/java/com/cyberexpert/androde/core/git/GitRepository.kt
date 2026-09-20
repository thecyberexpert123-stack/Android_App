package com.cyberexpert.androde.core.git

import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.ide.GitBranch
import com.cyberexpert.androde.domain.model.ide.GitCommit
import com.cyberexpert.androde.domain.model.ide.GitRepository
import com.cyberexpert.androde.domain.model.ide.GitStatus
import kotlinx.coroutines.flow.Flow
import java.io.File

interface GitRepository {
    suspend fun openRepository(root: File): AppResult<GitRepository>
    suspend fun getStatus(repo: GitRepository): AppResult<GitStatus>
    suspend fun getBranches(repo: GitRepository): AppResult<List<GitBranch>>
    suspend fun getCommits(repo: GitRepository, maxCount: Int = 50): AppResult<List<GitCommit>>
    suspend fun stageFile(repo: GitRepository, file: File): AppResult<Unit>
    suspend fun unstageFile(repo: GitRepository, file: File): AppResult<Unit>
    suspend fun stageAll(repo: GitRepository): AppResult<Unit>
    suspend fun commit(repo: GitRepository, message: String): AppResult<GitCommit>
    suspend fun push(repo: GitRepository): AppResult<Unit>
    suspend fun pull(repo: GitRepository): AppResult<Unit>
    suspend fun createBranch(repo: GitRepository, name: String): AppResult<GitBranch>
    suspend fun checkoutBranch(repo: GitRepository, branch: String): AppResult<Unit>
    fun watchStatus(repo: GitRepository): Flow<GitStatus>
}
