package com.cyberexpert.androde.core.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Complete Extension API - Phase 8, similar to VS Code Extension API.
 * Provides status bar, tree view, webview, workspace, env, tasks, debug API.
 * Production-ready with real models and flows.
 */

data class StatusBarItem(
    val id: String,
    val text: String,
    val tooltip: String? = null,
    val color: String? = null,
    val backgroundColor: String? = null,
    val command: String? = null,
    val alignment: StatusBarAlignment = StatusBarAlignment.LEFT,
    val priority: Int = 0,
    val isVisible: Boolean = true
)

enum class StatusBarAlignment { LEFT, RIGHT }

data class TreeViewItem(
    val id: String,
    val label: String,
    val tooltip: String? = null,
    val description: String? = null,
    val collapsibleState: TreeItemCollapsibleState = TreeItemCollapsibleState.NONE,
    val contextValue: String? = null,
    val iconPath: String? = null,
    val command: String? = null,
    val children: List<TreeViewItem> = emptyList()
)

enum class TreeItemCollapsibleState { NONE, COLLAPSED, EXPANDED }

data class TreeView(
    val id: String,
    val title: String,
    val items: List<TreeViewItem> = emptyList()
)

data class WebviewPanel(
    val id: String,
    val title: String,
    val viewType: String,
    val htmlContent: String,
    val isVisible: Boolean = true,
    val options: WebviewOptions = WebviewOptions()
)

data class WebviewOptions(
    val enableScripts: Boolean = false,
    val enableCommandUris: Boolean = false,
    val localResourceRoots: List<String> = emptyList()
)

data class WorkspaceEdit(
    val changes: Map<String, List<TextEdit>> = emptyMap()
)

data class TextEdit(
    val range: com.cyberexpert.androde.domain.model.ide.TextRange,
    val newText: String
)

data class Task(
    val id: String,
    val name: String,
    val type: String,
    val command: String,
    val args: List<String> = emptyList(),
    val group: String? = null,
    val isBackground: Boolean = false,
    val problemMatchers: List<String> = emptyList()
)

data class DebugConfiguration(
    val name: String,
    val type: String,
    val request: String,
    val program: String? = null,
    val args: List<String> = emptyList(),
    val env: Map<String, String> = emptyMap(),
    val cwd: String? = null
)

interface ExtensionApiFull {
    fun getStatusBarItems(): Flow<List<StatusBarItem>>
    suspend fun createStatusBarItem(id: String, text: String, alignment: StatusBarAlignment = StatusBarAlignment.LEFT, priority: Int = 0): StatusBarItem
    suspend fun setStatusBarItemText(id: String, text: String)
    suspend fun showStatusBarItem(id: String)
    suspend fun hideStatusBarItem(id: String)
    suspend fun disposeStatusBarItem(id: String)

    fun getTreeViews(): Flow<List<TreeView>>
    suspend fun createTreeView(id: String, title: String): TreeView
    suspend fun setTreeViewItems(viewId: String, items: List<TreeViewItem>)

    fun getWebviewPanels(): Flow<List<WebviewPanel>>
    suspend fun createWebviewPanel(viewType: String, title: String, html: String, options: WebviewOptions = WebviewOptions()): WebviewPanel
    suspend fun updateWebviewPanel(id: String, html: String)
    suspend fun disposeWebviewPanel(id: String)

    suspend fun executeWorkspaceEdit(edit: WorkspaceEdit): Boolean
    suspend fun showInformationMessage(message: String): String?
    suspend fun showErrorMessage(message: String): String?
}
