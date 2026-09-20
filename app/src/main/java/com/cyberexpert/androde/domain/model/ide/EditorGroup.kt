package com.cyberexpert.androde.domain.model.ide

import java.util.UUID

/**
 * Editor group, similar to VS Code editor groups.
 * Supports split editor (horizontal/vertical), multiple tabs per group.
 */
data class EditorGroup(
    val id: String = UUID.randomUUID().toString(),
    val tabs: List<EditorTab> = emptyList(),
    val activeTabId: String? = null,
    val isActive: Boolean = false
) {
    val activeTab: EditorTab? get() = tabs.find { it.id == activeTabId } ?: tabs.lastOrNull()
    val isEmpty: Boolean get() = tabs.isEmpty()
}

data class EditorGroupsState(
    val groups: List<EditorGroup> = listOf(EditorGroup(isActive = true)),
    val activeGroupId: String? = null
) {
    val activeGroup: EditorGroup? get() = groups.find { it.id == activeGroupId } ?: groups.firstOrNull { it.isActive } ?: groups.firstOrNull()
    val allTabs: List<EditorTab> get() = groups.flatMap { it.tabs }

    fun withSplit(newGroup: EditorGroup, direction: SplitDirection): EditorGroupsState {
        return copy(groups = groups + newGroup)
    }
}

enum class SplitDirection {
    HORIZONTAL, VERTICAL, LEFT, RIGHT, UP, DOWN
}

/**
 * Diagnostics / Problems, similar to VS Code Problems panel.
 */
data class Diagnostic(
    val id: String = UUID.randomUUID().toString(),
    val filePath: String,
    val fileName: String,
    val line: Int,
    val column: Int,
    val endLine: Int = line,
    val endColumn: Int = column,
    val severity: DiagnosticSeverity,
    val message: String,
    val source: String = "androde",
    val code: String? = null
)

enum class DiagnosticSeverity {
    ERROR, WARNING, INFO, HINT
}

data class DiagnosticsState(
    val diagnostics: List<Diagnostic> = emptyList(),
    val isLoading: Boolean = false
) {
    val errors: List<Diagnostic> get() = diagnostics.filter { it.severity == DiagnosticSeverity.ERROR }
    val warnings: List<Diagnostic> get() = diagnostics.filter { it.severity == DiagnosticSeverity.WARNING }
    val count: Int get() = diagnostics.size
}

/**
 * Output channel, similar to VS Code Output panel.
 */
data class OutputChannel(
    val id: String,
    val name: String,
    val lines: List<OutputLine> = emptyList()
)

data class OutputLine(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Workspace with multi-root support, similar to VS Code workspace.
 */
data class Workspace(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val folders: List<WorkspaceFolder> = emptyList(),
    val settings: Map<String, Any> = emptyMap()
)

data class WorkspaceFolder(
    val uri: String,
    val name: String,
    val path: String
)

/**
 * Debug models, similar to VS Code Run & Debug.
 */
data class DebugSession(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: String, // e.g., "kotlin", "java", "python"
    val isRunning: Boolean = false,
    val breakpoints: List<Breakpoint> = emptyList(),
    val variables: List<DebugVariable> = emptyList(),
    val callStack: List<CallStackFrame> = emptyList()
)

data class Breakpoint(
    val id: String = UUID.randomUUID().toString(),
    val filePath: String,
    val line: Int,
    val isEnabled: Boolean = true,
    val condition: String? = null
)

data class DebugVariable(
    val name: String,
    val value: String,
    val type: String,
    val variables: List<DebugVariable> = emptyList()
)

data class CallStackFrame(
    val id: String,
    val name: String,
    val filePath: String,
    val line: Int,
    val column: Int
)

/**
 * Keybinding, similar to VS Code keybindings.
 */
data class Keybinding(
    val id: String,
    val command: String,
    val key: String, // e.g., "Ctrl+Shift+P"
    val whenClause: String? = null
)

/**
 * Breadcrumb, similar to VS Code breadcrumbs.
 */
data class Breadcrumb(
    val filePath: String,
    val symbols: List<BreadcrumbSymbol> = emptyList()
)

data class BreadcrumbSymbol(
    val name: String,
    val kind: String, // class, function, variable, etc.
    val line: Int
)
