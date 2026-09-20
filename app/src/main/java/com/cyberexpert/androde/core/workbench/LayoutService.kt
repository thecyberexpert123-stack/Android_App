package com.cyberexpert.androde.core.workbench

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.cyberexpert.androde.core.base.DisposableBase
import com.cyberexpert.androde.core.base.Emitter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Layout Service - Similar to VS Code's src/vs/workbench/services/layout/browser/layoutService.ts
 * Manages workbench layout: visibility, position, dimensions of parts.
 * Production-ready with DataStore persistence, Flow, and events.
 */

interface ILayoutService {
    val layout: Flow<WorkbenchLayout>
    val onDidChangePartVisibility: Flow<WorkbenchPart>
    fun getLayout(): WorkbenchLayout
    fun isVisible(part: WorkbenchPart): Boolean
    fun setPartHidden(hidden: Boolean, part: WorkbenchPart)
    fun setPanelPosition(position: PartPosition)
    fun setSidebarPosition(position: SidebarPosition)
    fun setSidebarWidth(width: Int)
    fun setPanelHeight(height: Int)
    fun toggleSidebar()
    fun togglePanel()
    fun toggleActivityBar()
    fun toggleStatusBar()
    fun focusPart(part: WorkbenchPart)
    fun getContainer(part: WorkbenchPart): PartDimension?
}

@Singleton
class LayoutServiceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : DisposableBase(), ILayoutService {

    private object Keys {
        val SIDEBAR_VISIBLE = booleanPreferencesKey("layout_sidebar_visible")
        val SIDEBAR_POSITION = stringPreferencesKey("layout_sidebar_position")
        val SIDEBAR_WIDTH = intPreferencesKey("layout_sidebar_width")
        val PANEL_VISIBLE = booleanPreferencesKey("layout_panel_visible")
        val PANEL_POSITION = stringPreferencesKey("layout_panel_position")
        val PANEL_HEIGHT = intPreferencesKey("layout_panel_height")
        val ACTIVITYBAR_VISIBLE = booleanPreferencesKey("layout_activitybar_visible")
        val ACTIVITYBAR_POSITION = stringPreferencesKey("layout_activitybar_position")
        val STATUSBAR_VISIBLE = booleanPreferencesKey("layout_statusbar_visible")
        val AUXILIARYBAR_VISIBLE = booleanPreferencesKey("layout_auxiliarybar_visible")
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _layout = MutableStateFlow(WorkbenchLayout())
    override val layout: StateFlow<WorkbenchLayout> = _layout.asStateFlow()

    private val _onDidChangePartVisibility = Emitter<WorkbenchPart>()
    override val onDidChangePartVisibility: Flow<WorkbenchPart> = _onDidChangePartVisibility.event

    init {
        scope.launch {
            dataStore.data.collect { prefs ->
                val layout = WorkbenchLayout(
                    sidebarVisible = prefs[Keys.SIDEBAR_VISIBLE] ?: true,
                    sidebarPosition = SidebarPosition.fromString(prefs[Keys.SIDEBAR_POSITION] ?: "left"),
                    sidebarWidth = prefs[Keys.SIDEBAR_WIDTH] ?: 300,
                    panelVisible = prefs[Keys.PANEL_VISIBLE] ?: true,
                    panelPosition = PartPosition.fromString(prefs[Keys.PANEL_POSITION] ?: "bottom"),
                    panelHeight = prefs[Keys.PANEL_HEIGHT] ?: 300,
                    activityBarVisible = prefs[Keys.ACTIVITYBAR_VISIBLE] ?: true,
                    activityBarPosition = PartPosition.fromString(prefs[Keys.ACTIVITYBAR_POSITION] ?: "left"),
                    statusBarVisible = prefs[Keys.STATUSBAR_VISIBLE] ?: true,
                    auxiliaryBarVisible = prefs[Keys.AUXILIARYBAR_VISIBLE] ?: false
                )
                _layout.value = layout
                Log.d("LayoutService", "Loaded layout: $layout")
            }
        }
        Log.i("LayoutService", "Initialized layout service")
    }

    override fun getLayout(): WorkbenchLayout = _layout.value

    override fun isVisible(part: WorkbenchPart): Boolean = when (part) {
        WorkbenchPart.SIDEBAR -> _layout.value.sidebarVisible
        WorkbenchPart.PANEL -> _layout.value.panelVisible
        WorkbenchPart.ACTIVITYBAR -> _layout.value.activityBarVisible
        WorkbenchPart.STATUSBAR -> _layout.value.statusBarVisible
        WorkbenchPart.AUXILIARYBAR -> _layout.value.auxiliaryBarVisible
        WorkbenchPart.TITLEBAR -> _layout.value.titleBarVisible
        WorkbenchPart.EDITOR -> _layout.value.editorAreaVisible
        WorkbenchPart.BANNER -> true
    }

    override fun setPartHidden(hidden: Boolean, part: WorkbenchPart) {
        scope.launch {
            dataStore.edit { prefs ->
                when (part) {
                    WorkbenchPart.SIDEBAR -> prefs[Keys.SIDEBAR_VISIBLE] = !hidden
                    WorkbenchPart.PANEL -> prefs[Keys.PANEL_VISIBLE] = !hidden
                    WorkbenchPart.ACTIVITYBAR -> prefs[Keys.ACTIVITYBAR_VISIBLE] = !hidden
                    WorkbenchPart.STATUSBAR -> prefs[Keys.STATUSBAR_VISIBLE] = !hidden
                    WorkbenchPart.AUXILIARYBAR -> prefs[Keys.AUXILIARYBAR_VISIBLE] = !hidden
                    else -> {}
                }
            }
            _onDidChangePartVisibility.fire(part)
            Log.i("LayoutService", "Set part ${part.id} hidden=$hidden")
        }
    }

    override fun setPanelPosition(position: PartPosition) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.PANEL_POSITION] = position.name.lowercase()
            }
            Log.i("LayoutService", "Set panel position to $position")
        }
    }

    override fun setSidebarPosition(position: SidebarPosition) {
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.SIDEBAR_POSITION] = position.name.lowercase()
            }
            Log.i("LayoutService", "Set sidebar position to $position")
        }
    }

    override fun setSidebarWidth(width: Int) {
        val clamped = width.coerceIn(170, 600)
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.SIDEBAR_WIDTH] = clamped
            }
            Log.d("LayoutService", "Set sidebar width to $clamped")
        }
    }

    override fun setPanelHeight(height: Int) {
        val clamped = height.coerceIn(100, 800)
        scope.launch {
            dataStore.edit { prefs ->
                prefs[Keys.PANEL_HEIGHT] = clamped
            }
            Log.d("LayoutService", "Set panel height to $clamped")
        }
    }

    override fun toggleSidebar() {
        setPartHidden(isVisible(WorkbenchPart.SIDEBAR), WorkbenchPart.SIDEBAR)
    }

    override fun togglePanel() {
        setPartHidden(isVisible(WorkbenchPart.PANEL), WorkbenchPart.PANEL)
    }

    override fun toggleActivityBar() {
        setPartHidden(isVisible(WorkbenchPart.ACTIVITYBAR), WorkbenchPart.ACTIVITYBAR)
    }

    override fun toggleStatusBar() {
        setPartHidden(isVisible(WorkbenchPart.STATUSBAR), WorkbenchPart.STATUSBAR)
    }

    override fun focusPart(part: WorkbenchPart) {
        Log.i("LayoutService", "Focus part ${part.id}")
        // In real impl, would update focused part and notify
    }

    override fun getContainer(part: WorkbenchPart): PartDimension? {
        val layout = _layout.value
        return when (part) {
            WorkbenchPart.SIDEBAR -> PartDimension(layout.sidebarWidth, 0, layout.sidebarVisible, layout.sidebarPosition.toPartPosition())
            WorkbenchPart.PANEL -> PartDimension(0, layout.panelHeight, layout.panelVisible, layout.panelPosition)
            WorkbenchPart.ACTIVITYBAR -> PartDimension(48, 0, layout.activityBarVisible, layout.activityBarPosition)
            WorkbenchPart.STATUSBAR -> PartDimension(0, 22, layout.statusBarVisible, PartPosition.BOTTOM)
            else -> null
        }
    }

    override fun onDispose() {
        _onDidChangePartVisibility.dispose()
        Log.i("LayoutService", "Disposed layout service")
    }
}
