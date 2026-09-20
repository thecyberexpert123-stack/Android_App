package com.cyberexpert.androde.core.extensions

import kotlinx.coroutines.flow.Flow

/**
 * Extended Extension API - Phase 9 more complete, similar to VS Code extension API full.
 * Adds QuickPick, InputBox, Notifications, Progress, WorkspaceEdit, Commands, etc.
 */

data class QuickPickItem(
    val label: String,
    val description: String? = null,
    val detail: String? = null,
    val picked: Boolean = false,
    val alwaysShow: Boolean = false
)

data class InputBoxOptions(
    val prompt: String? = null,
    val placeHolder: String? = null,
    val value: String? = null,
    val password: Boolean = false,
    val ignoreFocusOut: Boolean = false
)

data class NotificationItem(
    val id: String,
    val message: String,
    val type: NotificationType = NotificationType.INFO,
    val actions: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)

enum class NotificationType { INFO, WARNING, ERROR }

data class ProgressOptions(
    val title: String,
    val location: String = "notification", // notification, window, sourceControl
    val cancellable: Boolean = false
)

data class CommandItem(
    val id: String,
    val title: String,
    val category: String? = null,
    val callback: String? = null
)

data class WorkspaceEditEntry(
    val filePath: String,
    val edits: List<com.cyberexpert.androde.core.extensions.TextEdit>
)

interface ExtensionApiExtended {
    fun getQuickPickItems(): Flow<List<QuickPickItem>>
    fun getNotifications(): Flow<List<NotificationItem>>
    fun getCommands(): Flow<List<CommandItem>>
    suspend fun showQuickPick(items: List<QuickPickItem>, placeHolder: String?): QuickPickItem?
    suspend fun showInputBox(options: InputBoxOptions): String?
    suspend fun showInformationMessage(message: String, vararg actions: String): String?
    suspend fun showWarningMessage(message: String, vararg actions: String): String?
    suspend fun showErrorMessage(message: String, vararg actions: String): String?
    suspend fun withProgress(options: ProgressOptions, task: suspend () -> Unit): Boolean
    suspend fun registerCommand(command: CommandItem): Boolean
    suspend fun executeCommand(commandId: String, vararg args: Any): Any?
    suspend fun applyWorkspaceEdit(edits: List<WorkspaceEditEntry>): Boolean
}
