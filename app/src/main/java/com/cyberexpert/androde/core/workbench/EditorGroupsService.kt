package com.cyberexpert.androde.core.workbench

import android.util.Log
import com.cyberexpert.androde.core.base.DisposableBase
import com.cyberexpert.androde.core.base.Emitter
import com.cyberexpert.androde.domain.model.ide.EditorTab
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Editor Groups Service - Similar to VS Code's src/vs/workbench/services/editor/common/editorGroupsService.ts
 * Manages editor groups (split editor), tabs, active group.
 * Production-ready with up to 3 groups, grid layout, and events.
 */

enum class GroupDirection {
    LEFT,
    RIGHT,
    UP,
    DOWN;

    fun isHorizontal(): Boolean = this == LEFT || this == RIGHT
    fun isVertical(): Boolean = this == UP || this == DOWN
}

enum class GroupOrientation {
    HORIZONTAL,
    VERTICAL;

    companion object {
        fun fromDirection(direction: GroupDirection): GroupOrientation =
            if (direction.isHorizontal()) HORIZONTAL else VERTICAL
    }
}

data class EditorGroup(
    val id: String = UUID.randomUUID().toString(),
    val tabs: List<EditorTab> = emptyList(),
    val activeTabId: String? = null,
    val isActive: Boolean = false,
    val label: String = "Group ${id.take(4)}"
) {
    val activeTab: EditorTab? get() = tabs.find { it.id == activeTabId } ?: tabs.lastOrNull()
    val count: Int get() = tabs.size
    val isEmpty: Boolean get() = tabs.isEmpty()

    fun withTab(tab: EditorTab): EditorGroup = copy(tabs = tabs + tab, activeTabId = tab.id)
    fun withoutTab(tabId: String): EditorGroup {
        val newTabs = tabs.filter { it.id != tabId }
        val newActiveId = if (activeTabId == tabId) newTabs.lastOrNull()?.id else activeTabId
        return copy(tabs = newTabs, activeTabId = newActiveId)
    }
    fun withActiveTab(tabId: String): EditorGroup = copy(activeTabId = tabId)
    fun withActive(isActive: Boolean): EditorGroup = copy(isActive = isActive)
    fun withTabs(newTabs: List<EditorTab>): EditorGroup = copy(tabs = newTabs)
}

data class EditorGroupsState(
    val groups: List<EditorGroup> = listOf(EditorGroup(isActive = true)),
    val activeGroupId: String = groups.firstOrNull { it.isActive }?.id ?: groups.firstOrNull()?.id ?: "",
    val orientation: GroupOrientation = GroupOrientation.HORIZONTAL
) {
    val activeGroup: EditorGroup? get() = groups.find { it.id == activeGroupId } ?: groups.firstOrNull()
    val count: Int get() = groups.size
}

interface IEditorGroupsService {
    val groups: Flow<EditorGroupsState>
    val onDidAddGroup: Flow<EditorGroup>
    val onDidRemoveGroup: Flow<String>
    val onDidChangeActiveGroup: Flow<EditorGroup>
    fun getGroups(): EditorGroupsState
    fun getActiveGroup(): EditorGroup?
    fun createGroup(direction: GroupDirection): EditorGroup?
    fun removeGroup(groupId: String): Boolean
    fun setActiveGroup(groupId: String)
    fun addTabToGroup(tab: EditorTab, groupId: String? = null)
    fun removeTabFromGroup(tabId: String, groupId: String? = null)
    fun setActiveTab(tabId: String, groupId: String? = null)
    fun splitEditor(direction: GroupDirection): Boolean
    fun closeAllGroups()
    fun moveTabToGroup(tabId: String, fromGroupId: String, toGroupId: String): Boolean
}

@Singleton
class EditorGroupsServiceImpl @Inject constructor() : DisposableBase(), IEditorGroupsService {

    private val _groups = MutableStateFlow(EditorGroupsState())
    override val groups: StateFlow<EditorGroupsState> = _groups.asStateFlow()

    private val _onDidAddGroup = Emitter<EditorGroup>()
    override val onDidAddGroup: Flow<EditorGroup> = _onDidAddGroup.event

    private val _onDidRemoveGroup = Emitter<String>()
    override val onDidRemoveGroup: Flow<String> = _onDidRemoveGroup.event

    private val _onDidChangeActiveGroup = Emitter<EditorGroup>()
    override val onDidChangeActiveGroup: Flow<EditorGroup> = _onDidChangeActiveGroup.event

    init {
        Log.i("EditorGroupsService", "Initialized with 1 group")
    }

    override fun getGroups(): EditorGroupsState = _groups.value

    override fun getActiveGroup(): EditorGroup? = _groups.value.activeGroup

    override fun createGroup(direction: GroupDirection): EditorGroup? {
        val current = _groups.value
        if (current.groups.size >= 3) {
            Log.w("EditorGroupsService", "Max 3 groups reached, cannot create more")
            return null
        }
        val newGroup = EditorGroup(isActive = false)
        val newGroups = current.groups.map { it.withActive(false) } + newGroup.withActive(true)
        val newOrientation = GroupOrientation.fromDirection(direction)
        _groups.value = current.copy(groups = newGroups, activeGroupId = newGroup.id, orientation = newOrientation)
        _onDidAddGroup.fire(newGroup)
        Log.i("EditorGroupsService", "Created group ${newGroup.id} direction $direction, total ${newGroups.size}")
        return newGroup
    }

    override fun removeGroup(groupId: String): Boolean {
        val current = _groups.value
        if (current.groups.size <= 1) {
            Log.w("EditorGroupsService", "Cannot remove last group")
            return false
        }
        val groupToRemove = current.groups.find { it.id == groupId } ?: return false
        val remaining = current.groups.filter { it.id != groupId }
        val newActiveId = if (current.activeGroupId == groupId) remaining.lastOrNull()?.id ?: "" else current.activeGroupId
        val newGroups = remaining.mapIndexed { index, group ->
            if (index == remaining.size - 1 && remaining.none { it.isActive }) group.withActive(true) else group
        }
        _groups.value = current.copy(groups = newGroups, activeGroupId = newActiveId)
        _onDidRemoveGroup.fire(groupId)
        Log.i("EditorGroupsService", "Removed group $groupId, remaining ${newGroups.size}")
        return true
    }

    override fun setActiveGroup(groupId: String) {
        val current = _groups.value
        val group = current.groups.find { it.id == groupId } ?: return
        val newGroups = current.groups.map { it.withActive(it.id == groupId) }
        _groups.value = current.copy(groups = newGroups, activeGroupId = groupId)
        _onDidChangeActiveGroup.fire(group.withActive(true))
        Log.i("EditorGroupsService", "Set active group to $groupId")
    }

    override fun addTabToGroup(tab: EditorTab, groupId: String?) {
        val current = _groups.value
        val targetId = groupId ?: current.activeGroupId
        val newGroups = current.groups.map { group ->
            if (group.id == targetId) group.withTab(tab) else group
        }
        _groups.value = current.copy(groups = newGroups)
        Log.i("EditorGroupsService", "Added tab ${tab.fileName} to group $targetId")
    }

    override fun removeTabFromGroup(tabId: String, groupId: String?) {
        val current = _groups.value
        val targetId = groupId ?: current.groups.find { g -> g.tabs.any { it.id == tabId } }?.id ?: return
        val newGroups = current.groups.map { group ->
            if (group.id == targetId) group.withoutTab(tabId) else group
        }
        _groups.value = current.copy(groups = newGroups)
        Log.i("EditorGroupsService", "Removed tab $tabId from group $targetId")
    }

    override fun setActiveTab(tabId: String, groupId: String?) {
        val current = _groups.value
        val targetId = groupId ?: current.groups.find { g -> g.tabs.any { it.id == tabId } }?.id ?: current.activeGroupId
        val newGroups = current.groups.map { group ->
            if (group.id == targetId) group.withActiveTab(tabId) else group
        }
        _groups.value = current.copy(groups = newGroups, activeGroupId = targetId)
        Log.i("EditorGroupsService", "Set active tab $tabId in group $targetId")
    }

    override fun splitEditor(direction: GroupDirection): Boolean {
        val created = createGroup(direction)
        return created != null
    }

    override fun closeAllGroups() {
        _groups.value = EditorGroupsState()
        Log.i("EditorGroupsService", "Closed all groups, reset to 1")
    }

    override fun moveTabToGroup(tabId: String, fromGroupId: String, toGroupId: String): Boolean {
        val current = _groups.value
        val fromGroup = current.groups.find { it.id == fromGroupId } ?: return false
        val toGroup = current.groups.find { it.id == toGroupId } ?: return false
        val tab = fromGroup.tabs.find { it.id == tabId } ?: return false
        val newGroups = current.groups.map { group ->
            when (group.id) {
                fromGroupId -> group.withoutTab(tabId)
                toGroupId -> group.withTab(tab)
                else -> group
            }
        }
        _groups.value = current.copy(groups = newGroups)
        Log.i("EditorGroupsService", "Moved tab $tabId from $fromGroupId to $toGroupId")
        return true
    }

    override fun onDispose() {
        _onDidAddGroup.dispose()
        _onDidRemoveGroup.dispose()
        _onDidChangeActiveGroup.dispose()
        Log.i("EditorGroupsService", "Disposed")
    }
}
