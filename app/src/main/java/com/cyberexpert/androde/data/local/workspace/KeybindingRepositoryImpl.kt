package com.cyberexpert.androde.data.local.workspace

import com.cyberexpert.androde.core.keybinding.KeybindingRepository
import com.cyberexpert.androde.domain.model.ide.Keybinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeybindingRepositoryImpl @Inject constructor() : KeybindingRepository {

    private val _keybindings = MutableStateFlow<List<Keybinding>>(getDefaultKeybindings())

    override fun getKeybindings(): Flow<List<Keybinding>> = _keybindings.asStateFlow()

    override suspend fun setKeybinding(keybinding: Keybinding) {
        _keybindings.value = _keybindings.value.filter { it.id != keybinding.id } + keybinding
    }

    override suspend fun removeKeybinding(id: String) {
        _keybindings.value = _keybindings.value.filter { it.id != id }
    }

    override fun getDefaultKeybindings(): List<Keybinding> {
        return listOf(
            Keybinding("openFolder", "workbench.action.files.openFolder", "Ctrl+O"),
            Keybinding("newFile", "workbench.action.files.newUntitledFile", "Ctrl+N"),
            Keybinding("save", "workbench.action.files.save", "Ctrl+S"),
            Keybinding("saveAll", "workbench.action.files.saveAll", "Ctrl+K S"),
            Keybinding("closeEditor", "workbench.action.closeActiveEditor", "Ctrl+W"),
            Keybinding("splitEditor", "workbench.action.splitEditor", "Ctrl+\\"),
            Keybinding("findInFiles", "workbench.action.findInFiles", "Ctrl+Shift+F"),
            Keybinding("toggleTerminal", "workbench.action.terminal.toggleTerminal", "Ctrl+`"),
            Keybinding("showExplorer", "workbench.view.explorer", "Ctrl+Shift+E"),
            Keybinding("showSearch", "workbench.view.search", "Ctrl+Shift+F"),
            Keybinding("showSCM", "workbench.view.scm", "Ctrl+Shift+G"),
            Keybinding("showExtensions", "workbench.view.extensions", "Ctrl+Shift+X"),
            Keybinding("showCommands", "workbench.action.showCommands", "Ctrl+Shift+P"),
            Keybinding("openSettings", "workbench.action.openSettings", "Ctrl+,"),
            Keybinding("selectTheme", "workbench.action.selectTheme", "Ctrl+K Ctrl+T"),
            Keybinding("formatDocument", "editor.action.formatDocument", "Shift+Alt+F"),
            Keybinding("goToLine", "workbench.action.gotoLine", "Ctrl+G")
        )
    }
}
