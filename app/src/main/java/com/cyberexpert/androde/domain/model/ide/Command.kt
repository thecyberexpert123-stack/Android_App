package com.cyberexpert.androde.domain.model.ide

/**
 * Command for Command Palette, similar to VS Code Ctrl+Shift+P.
 */
data class Command(
    val id: String,
    val label: String,
    val description: String? = null,
    val category: String? = null,
    val keybinding: String? = null,
    val icon: String? = null,
    val action: CommandAction = CommandAction.NOOP
)

enum class CommandAction {
    NOOP,
    OPEN_FOLDER,
    NEW_FILE,
    NEW_FOLDER,
    SAVE_FILE,
    SAVE_ALL,
    CLOSE_FILE,
    CLOSE_ALL,
    FIND,
    REPLACE,
    GO_TO_LINE,
    TOGGLE_TERMINAL,
    TOGGLE_EXPLORER,
    TOGGLE_SEARCH,
    TOGGLE_GIT,
    TOGGLE_EXTENSIONS,
    COMMAND_PALETTE,
    SETTINGS,
    SPLIT_EDITOR,
    FORMAT_DOCUMENT,
    GIT_COMMIT,
    GIT_PUSH,
    GIT_PULL,
    TERMINAL_CLEAR,
    SEARCH_IN_FILES,
    OPEN_SETTINGS,
    CHANGE_THEME
}

/**
 * Built-in commands, similar to VS Code default commands.
 */
object BuiltinCommands {
    val all = listOf(
        Command("workbench.action.files.openFolder", "File: Open Folder", "Open a folder", "File", null, CommandAction.OPEN_FOLDER),
        Command("workbench.action.files.newUntitledFile", "File: New Untitled File", "Create new file", "File", "Ctrl+N", CommandAction.NEW_FILE),
        Command("workbench.action.files.newFolder", "File: New Folder", "Create new folder", "File", null, CommandAction.NEW_FOLDER),
        Command("workbench.action.files.save", "File: Save", "Save current file", "File", "Ctrl+S", CommandAction.SAVE_FILE),
        Command("workbench.action.files.saveAll", "File: Save All", "Save all files", "File", "Ctrl+K S", CommandAction.SAVE_ALL),
        Command("workbench.action.closeActiveEditor", "View: Close Editor", "Close current editor", "View", "Ctrl+W", CommandAction.CLOSE_FILE),
        Command("workbench.action.closeAllEditors", "View: Close All Editors", "Close all editors", "View", "Ctrl+K Ctrl+W", CommandAction.CLOSE_ALL),
        Command("workbench.action.splitEditor", "View: Split Editor", "Split editor", "View", "Ctrl+\\", CommandAction.SPLIT_EDITOR),
        Command("workbench.action.findInFiles", "Search: Find in Files", "Search across files", "Search", "Ctrl+Shift+F", CommandAction.SEARCH_IN_FILES),
        Command("workbench.action.terminal.toggleTerminal", "View: Toggle Terminal", "Show/hide terminal", "View", "Ctrl+`", CommandAction.TOGGLE_TERMINAL),
        Command("workbench.view.explorer", "View: Show Explorer", "Show file explorer", "View", "Ctrl+Shift+E", CommandAction.TOGGLE_EXPLORER),
        Command("workbench.view.search", "View: Show Search", "Show search", "View", "Ctrl+Shift+F", CommandAction.TOGGLE_SEARCH),
        Command("workbench.view.scm", "View: Show Source Control", "Show git", "View", "Ctrl+Shift+G", CommandAction.TOGGLE_GIT),
        Command("workbench.view.extensions", "View: Show Extensions", "Show extensions", "View", "Ctrl+Shift+X", CommandAction.TOGGLE_EXTENSIONS),
        Command("workbench.action.showCommands", "Show All Commands", "Open command palette", "General", "Ctrl+Shift+P", CommandAction.COMMAND_PALETTE),
        Command("workbench.action.openSettings", "Preferences: Open Settings", "Open settings", "Preferences", "Ctrl+,", CommandAction.OPEN_SETTINGS),
        Command("workbench.action.selectTheme", "Preferences: Color Theme", "Change theme", "Preferences", "Ctrl+K Ctrl+T", CommandAction.CHANGE_THEME),
        Command("editor.action.formatDocument", "Format Document", "Format current file", "Editor", "Shift+Alt+F", CommandAction.FORMAT_DOCUMENT),
        Command("workbench.action.terminal.clear", "Terminal: Clear", "Clear terminal", "Terminal", null, CommandAction.TERMINAL_CLEAR),
        Command("git.commit", "Git: Commit", "Commit changes", "Git", "Ctrl+Enter", CommandAction.GIT_COMMIT),
        Command("git.push", "Git: Push", "Push to remote", "Git", null, CommandAction.GIT_PUSH),
        Command("git.pull", "Git: Pull", "Pull from remote", "Git", null, CommandAction.GIT_PULL)
    )
}
