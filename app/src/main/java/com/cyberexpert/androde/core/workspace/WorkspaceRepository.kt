package com.cyberexpert.androde.core.workspace

import com.cyberexpert.androde.domain.model.ide.Workspace
import com.cyberexpert.androde.domain.model.ide.WorkspaceFolder
import kotlinx.coroutines.flow.Flow

interface WorkspaceRepository {
    fun getWorkspace(): Flow<Workspace?>
    fun getRecentWorkspaces(): Flow<List<Workspace>>
    suspend fun openWorkspace(folder: WorkspaceFolder): Workspace
    suspend fun openMultiRoot(folders: List<WorkspaceFolder>, name: String): Workspace
    suspend fun addFolderToWorkspace(folder: WorkspaceFolder)
    suspend fun removeFolderFromWorkspace(folderUri: String)
    suspend fun closeWorkspace()
    suspend fun saveWorkspace(workspace: Workspace)
}
