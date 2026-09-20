package com.cyberexpert.androde.core.workbench

import kotlinx.coroutines.flow.Flow

/**
 * Workbench - Main application model similar to VS Code's src/vs/workbench/browser/workbench.ts
 * Orchestrates all UI parts, services, and contributions.
 * Production-ready with layout, editor groups, and lifecycle.
 */

data class WorkbenchState(
    val layout: WorkbenchLayout = WorkbenchLayout(),
    val isRestored: Boolean = false,
    val isReady: Boolean = false,
    val activePart: WorkbenchPart = WorkbenchPart.EDITOR,
    val focusedPart: WorkbenchPart? = null
)

interface IWorkbench {
    val state: Flow<WorkbenchState>
    val layout: Flow<WorkbenchLayout>
    fun getState(): WorkbenchState
    fun getLayout(): WorkbenchLayout
    fun setReady(ready: Boolean)
    fun setRestored(restored: Boolean)
    fun setActivePart(part: WorkbenchPart)
    fun setFocusedPart(part: WorkbenchPart?)
    fun toggleSidebar()
    fun togglePanel()
    fun toggleActivityBar()
    fun toggleStatusBar()
    fun setSidebarPosition(position: SidebarPosition)
    fun setPanelPosition(position: PartPosition)
    fun setSidebarWidth(width: Int)
    fun setPanelHeight(height: Int)
}
