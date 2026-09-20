package com.cyberexpert.androde.core.workbench

/**
 * Workbench Parts - Similar to VS Code's src/vs/workbench/services/layout/browser/layoutService.ts
 * Parts enum defines all workbench UI parts for layout management.
 * Production-ready with positions, visibility, and dimensions.
 */

enum class WorkbenchPart(val id: String) {
    TITLEBAR("workbench.parts.titlebar"),
    BANNER("workbench.parts.banner"),
    ACTIVITYBAR("workbench.parts.activitybar"),
    SIDEBAR("workbench.parts.sidebar"),
    AUXILIARYBAR("workbench.parts.auxiliarybar"),
    EDITOR("workbench.parts.editor"),
    PANEL("workbench.parts.panel"),
    STATUSBAR("workbench.parts.statusbar");

    companion object {
        fun fromId(id: String): WorkbenchPart? = entries.find { it.id == id }
    }
}

enum class PartPosition {
    LEFT,
    RIGHT,
    BOTTOM,
    TOP;

    companion object {
        fun fromString(value: String): PartPosition = when (value.lowercase()) {
            "left" -> LEFT
            "right" -> RIGHT
            "bottom" -> BOTTOM
            "top" -> TOP
            else -> LEFT
        }
    }
}

enum class SidebarPosition {
    LEFT,
    RIGHT;

    fun toPartPosition(): PartPosition = when (this) {
        LEFT -> PartPosition.LEFT
        RIGHT -> PartPosition.RIGHT
    }

    companion object {
        fun fromString(value: String): SidebarPosition = when (value.lowercase()) {
            "right" -> RIGHT
            else -> LEFT
        }
    }
}

data class PartDimension(
    val width: Int,
    val height: Int,
    val isVisible: Boolean,
    val position: PartPosition
)

data class WorkbenchLayout(
    val activityBarVisible: Boolean = true,
    val activityBarPosition: PartPosition = PartPosition.LEFT,
    val sidebarVisible: Boolean = true,
    val sidebarPosition: SidebarPosition = SidebarPosition.LEFT,
    val sidebarWidth: Int = 300,
    val auxiliaryBarVisible: Boolean = false,
    val auxiliaryBarPosition: SidebarPosition = SidebarPosition.RIGHT,
    val auxiliaryBarWidth: Int = 300,
    val panelVisible: Boolean = true,
    val panelPosition: PartPosition = PartPosition.BOTTOM,
    val panelHeight: Int = 300,
    val statusBarVisible: Boolean = true,
    val titleBarVisible: Boolean = true,
    val editorAreaVisible: Boolean = true
) {
    fun withSidebarVisible(visible: Boolean): WorkbenchLayout = copy(sidebarVisible = visible)
    fun withPanelVisible(visible: Boolean): WorkbenchLayout = copy(panelVisible = visible)
    fun withActivityBarVisible(visible: Boolean): WorkbenchLayout = copy(activityBarVisible = visible)
    fun withStatusBarVisible(visible: Boolean): WorkbenchLayout = copy(statusBarVisible = visible)
    fun withSidebarPosition(position: SidebarPosition): WorkbenchLayout = copy(sidebarPosition = position)
    fun withPanelPosition(position: PartPosition): WorkbenchLayout = copy(panelPosition = position)
    fun withSidebarWidth(width: Int): WorkbenchLayout = copy(sidebarWidth = width.coerceIn(170, 600))
    fun withPanelHeight(height: Int): WorkbenchLayout = copy(panelHeight = height.coerceIn(100, 800))
}
