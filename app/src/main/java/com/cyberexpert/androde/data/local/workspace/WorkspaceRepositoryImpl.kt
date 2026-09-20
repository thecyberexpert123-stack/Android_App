package com.cyberexpert.androde.data.local.workspace

import com.cyberexpert.androde.core.workspace.WorkspaceRepository
import com.cyberexpert.androde.domain.model.ide.Workspace
import com.cyberexpert.androde.domain.model.ide.WorkspaceFolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkspaceRepositoryImpl @Inject constructor() : WorkspaceRepository {

    private val _workspace = MutableStateFlow<Workspace?>(null)
    private val _recent = MutableStateFlow<List<Workspace>>(emptyList())

    override fun getWorkspace(): Flow<Workspace?> = _workspace.asStateFlow()
    override fun getRecentWorkspaces(): Flow<List<Workspace>> = _recent.asStateFlow()

    override suspend fun openWorkspace(folder: WorkspaceFolder): Workspace {
        val workspace = Workspace(
            name = folder.name,
            folders = listOf(folder)
        )
        _workspace.value = workspace
        _recent.value = (listOf(workspace) + _recent.value).distinctBy { it.folders.firstOrNull()?.path }.take(10)
        return workspace
    }

    override suspend fun openMultiRoot(folders: List<WorkspaceFolder>, name: String): Workspace {
        val workspace = Workspace(name = name, folders = folders)
        _workspace.value = workspace
        _recent.value = (listOf(workspace) + _recent.value).take(10)
        return workspace
    }

    override suspend fun addFolderToWorkspace(folder: WorkspaceFolder) {
        val current = _workspace.value ?: return
        _workspace.value = current.copy(folders = current.folders + folder)
    }

    override suspend fun removeFolderFromWorkspace(folderUri: String) {
        val current = _workspace.value ?: return
        _workspace.value = current.copy(folders = current.folders.filter { it.uri != folderUri })
    }

    override suspend fun closeWorkspace() {
        _workspace.value = null
    }

    override suspend fun saveWorkspace(workspace: Workspace) {
        _workspace.value = workspace
    }
}
