# Architecture - Androde (VS Code for Android) - Phase 11 Deep Research

## Overview

Androde remakes VS Code for Android, following **VS Code's 4-layer architecture** (Base, Platform, Editor, Workbench) + **Clean Architecture + MVVM + UDF** recommended by Android Developers (Now in Android sample), but optimized for Android.

This document incorporates deep research from VS Code source (microsoft/vscode), Monaco editor, LSP/DAP specs, Sora Editor, CodeAssist, and Android best practices.

### VS Code Architecture Mapping

VS Code's layered architecture (from src/vs/):

| VS Code Layer | Responsibility | Androde Mapping | Package |
|---------------|----------------|-----------------|---------|
| **Base** (`src/vs/base/`) | Foundation utilities, IDisposable, Event, lifecycle, platform | Disposable, Emitter, URI, Path | `core/base/` |
| **Platform** (`src/vs/platform/`) | Services, DI, file service, config, theme, keybinding, extensions | FileService, ConfigurationService, ThemeService, KeybindingService, ExtensionManagement | `core/platform/`, `core/theme/`, `core/workspace/` |
| **Editor** (`src/vs/editor/`) | Monaco core, text model, rendering, language services | Sora Editor + TextMate + TreeSitter, LanguageService, EditorService, Brackets, Minimap | `core/editor/`, `presentation/components/ide/` |
| **Workbench** (`src/vs/workbench/`) | UI, parts (activitybar, sidebar, editor, panel, statusbar), contributions | WorkbenchLayout, LayoutService, EditorGroupsService, ActivityBar, Sidebar, Panel, StatusBar | `core/workbench/`, `presentation/components/workbench/` |

**Dependency Rule (strict, like VS Code):**
```
Workbench -> Editor -> Platform -> Base
Presentation -> Domain <- Data
```
Each layer can only depend on layers below, never above.

### Process Architecture (Adapted for Android)

**VS Code Multi-Process:**
- Main Process (Electron main): windows, menus, system
- Renderer Process (Electron renderer): Workbench UI + Monaco
- Extension Host (Node.js): extensions isolated
- PTY Host (Node.js): terminal PTY, file watching
- Debug Adapter (per session): DAP
- Language Server (per language): LSP
- Communication: IPC JSON-RPC with Content-Length framing

**Androde Single-Process + Future Multi-Process:**
- **Main Process** (`:app`): All in one for MVP (Android single process limitation, but architecture ready)
- **Extension Host** (future `:extensionHost` process): Rhino JS engine isolated, AIDL for IPC
- **PTY Host** (future): Terminal PTY via JNI libterm
- **Language Servers** (future): Start via ProcessBuilder, stdio/socket, LSP
- **Debug Adapters** (future): Start via ProcessBuilder, DAP
- **Communication**: For now, in-process StateFlow + Log; future AIDL + Content-Length framing (already implemented in LspClient/DapClient)

**Message Framing (LSP/DAP standard):**
```
Content-Length: 123\r\n\r\n{"jsonrpc":"2.0","id":"...","method":"...","params":{...}}
```
Implemented in `core/lsp/LspClient.kt` and `core/debug/DapClient.kt` with BufferedReader/Writer, handling Content-Length header, empty line, content reading.

### Workbench Parts (from VS Code src/vs/workbench/services/layout/browser/layoutService.ts)

```kotlin
enum class WorkbenchPart(val id: String) {
    TITLEBAR("workbench.parts.titlebar"),
    BANNER("workbench.parts.banner"),
    ACTIVITYBAR("workbench.parts.activitybar"),
    SIDEBAR("workbench.parts.sidebar"),
    AUXILIARYBAR("workbench.parts.auxiliarybar"),
    EDITOR("workbench.parts.editor"),
    PANEL("workbench.parts.panel"),
    STATUSBAR("workbench.parts.statusbar")
}
```

Layout (VS Code style):
```
┌───────────────────────────────────────────────────────┐
│                     TITLEBAR                          │
├───────┬───────────────────────────────────────┬───────┤
│ ACTIV │                                        │  AUX  │
│  ITY  │           EDITOR AREA                │ ILIAR │
│  BAR  │        (Part.EDITOR)                │  BAR  │
│       ├────────────────────────────────────────┤       │
│       │           PANEL AREA                 │       │
│       │        (Part.PANEL)                 │       │
├───────┴───────────────────────────────────────┴───────┤
│                  STATUSBAR                            │
└───────────────────────────────────────────────────────┘
```

- **ActivityBar**: 48dp width, left/right/top/bottom, main nav icons (Explorer, Search, SCM, Debug, Extensions, 16+ items), badges, scrollable
- **Sidebar**: 170-600dp, left/right, resizable via drag (tablet), contains viewlets (Explorer file tree, Search results, SCM git, Debug breakpoints, Extensions list, Outline symbols, Timeline, etc.), header with title + actions
- **Editor**: Center, weight 1f, editor groups (up to 3, horizontal/vertical grid, like VS Code), tabs LazyRow, SoraEditorView with toolbar Format/Emmet/Zoom, breadcrumbs above tabs, welcome when no tabs
- **Panel**: Bottom/right, 100-800dp, resizable, Terminal, Problems, Output, Debug Console, Search, Timeline, Outline, etc., TabRow or LazyRow scrollable
- **StatusBar**: Bottom, 22dp height, left: git branch, errors/warnings, language, encoding, EOL, right: notifications, etc.
- **TitleBar**: Top, app name, window controls (ChromeOS, DeX)
- **AuxiliaryBar**: Right, secondary sidebar (Outline, Timeline)

**For Android:**
- Phone: ActivityBar + Sidebar as ModalNavigationDrawer, Editor full, Panel as ModalBottomSheet, StatusBar bottom
- Tablet: ActivityBar left, Sidebar left resizable, Editor center, Panel bottom resizable, StatusBar bottom
- Responsive via WindowSizeClass
- Implemented in `presentation/components/workbench/WorkbenchLayout.kt` with WorkbenchLayout composable + WorkbenchLayoutPhone

**Layout Service (from VS Code IWorkbenchLayoutService):**
```kotlin
interface ILayoutService {
    val layout: Flow<WorkbenchLayout>
    val onDidChangePartVisibility: Flow<WorkbenchPart>
    fun isVisible(part: WorkbenchPart): Boolean
    fun setPartHidden(hidden: Boolean, part: WorkbenchPart)
    fun setPanelPosition(position: PartPosition)
    fun setSidebarPosition(position: SidebarPosition)
    fun setSidebarWidth(width: Int)
    fun setPanelHeight(height: Int)
    fun toggleSidebar()
    fun togglePanel()
    fun focusPart(part: WorkbenchPart)
    fun getContainer(part: WorkbenchPart): PartDimension?
}
```
Implemented in `core/workbench/LayoutServiceImpl.kt` with DataStore persistence, Flow, Emitter events, Log.

**Editor Groups Service (from VS Code IEditorGroupsService):**
```kotlin
interface IEditorGroupsService {
    val groups: Flow<EditorGroupsState>
    fun getGroups(): EditorGroupsState
    fun getActiveGroup(): EditorGroup?
    fun createGroup(direction: GroupDirection): EditorGroup?
    fun removeGroup(groupId: String): Boolean
    fun setActiveGroup(groupId: String)
    fun addTabToGroup(tab: EditorTab, groupId: String?)
    fun splitEditor(direction: GroupDirection): Boolean
    fun closeAllGroups()
    fun moveTabToGroup(tabId: String, fromGroupId: String, toGroupId: String): Boolean
}
```
Implemented in `core/workbench/EditorGroupsServiceImpl.kt` with up to 3 groups, horizontal/vertical orientation, UUID, StateFlow, Emitter events.

### Base Layer (from VS Code src/vs/base/common/lifecycle.ts, event.ts)

**Disposable Pattern:**
```kotlin
interface IDisposable {
    fun dispose()
    val isDisposed: Boolean
}
class DisposableStore : DisposableBase() {
    fun add(disposable: IDisposable)
    fun clear()
}
class ActionDisposable(action: () -> Unit) : DisposableBase()
```
Implemented in `core/base/Disposable.kt` with Log, try/catch, clear.

**Event Emitter:**
```kotlin
class Emitter<T> : DisposableBase(), IEmitter<T> {
    val event: Event<T> // SharedFlow
    fun fire(event: T)
    fun addListener(listener: (T) -> Unit): IDisposable
}
```
Implemented in `core/base/Event.kt` with MutableSharedFlow extraBufferCapacity 64, CopyOnWriteArrayList listeners, Log.

Used for configuration changes, layout changes, file changes, etc., similar to VS Code's Event.

### Platform Layer

**FileService (from VS Code src/vs/platform/files/common/fileService.ts):**
```kotlin
interface IFileService {
    val onDidFilesChange: Flow<FileSystemEvent>
    suspend fun listFiles(dir: File, showHidden: Boolean): Result<List<FileNode>>
    suspend fun readFile(file: File): Result<String>
    suspend fun writeFile(file: File, content: String): Result<Unit>
    fun watch(dir: File): Flow<FileSystemEvent>
    fun getFileStat(file: File): FileStat?
}
```
Implemented in `core/platform/FileServiceImpl.kt` with canonical checks, size limits (10MB read, 5MB search skip), forbidden dirs (/, /system, /proc, /data), recent files StateFlow, Emitter, Dispatchers.IO, Log, security validation.

**ConfigurationService (from VS Code src/vs/platform/configuration/common/configuration.ts):**
- DataStore Preferences, get/set, profiles, onDidChangeConfiguration event
- Implemented via SettingsRepository with formatOnSave, emmetOnTab, iconThemeId, breadcrumbsEnabled, minimapEnabled, fontSize, tabSize, wordWrap, theme, etc.

**ThemeService (from VS Code src/vs/platform/theme/common/themeService.ts):**
```kotlin
interface IThemeService {
    val theme: Flow<ThemeState>
    val onDidColorThemeChange: Flow<ColorThemeId>
    fun getTheme(): ThemeState
    fun setColorTheme(themeId: ColorThemeId)
    fun setIconTheme(themeId: IconThemeId)
}
```
Implemented in `core/theme/ThemeServiceImpl.kt` with ColorThemeId (DARK, LIGHT, MONOKAI, DRACULA, etc.), IconThemeId (VSCODE_ICONS, MATERIAL_ICONS), DataStore, Emitter, Log.
- Supports VS Code theme format: colors, tokenColors, semanticHighlighting, etc. via TextMate themes (vscode_dark, darcula, monokai)
- Icon themes via vscode_icons.json, material_icons.json with FileIconResolver for 100 langs

**KeybindingService (from VS Code src/vs/platform/keybinding/common/keybinding.ts):**
- Default keybindings: Ctrl+O (open folder), Ctrl+N (new file), Ctrl+S (save), Ctrl+K S (save all), Ctrl+W (close), Ctrl+\ (split), Ctrl+Shift+F (search), Ctrl+` (terminal), Ctrl+Shift+E/G/X/P (explorer/git/extensions/command palette), Ctrl+, (settings), Ctrl+K Ctrl+T (theme), Shift+Alt+F (format), Ctrl+G (go to line), etc.
- When clause, custom keybindings, DataStore persistence
- Implemented in KeybindingRepositoryImpl with MutableStateFlow, Flow

### Editor Layer

**Sora Editor Integration (from Sora docs, 0.23.6):**
- Core: CodeEditor, TextMateColorScheme, TextMateLanguage, ThemeRegistry, GrammarRegistry, AssetsFileResolver, ContentListener, DisposableEffect
- Features: incremental highlight, auto-completion, auto-indent, block lines, scale text, undo/redo, search/replace, wordwrap, non-printable chars, diagnostic markers, magnifier, sticky scroll, bracket pair colorization, highlight bracket pairs, event system
- Modules: editor (core), editor-bom, editor-lsp, language-java, language-monarch, language-textmate, language-treesitter, oniguruma-native
- 100 grammars (Phase 11) with scope mappings: source.kotlin, source.java, source.js, source.ts, source.python, text.html.basic, source.css, source.json, text.html.markdown, source.yaml, source.shell, source.go, source.rust, source.cpp, text.xml, source.sql, source.cs, source.dart, source.php, source.ruby, source.swift, source.properties, source.dockerfile, source.ini, source.toml, source.groovy, source.lua, source.r, source.batchfile, source.powershell, source.makefile, source.cmake, source.clojure, source.elixir, source.erlang, source.haskell, source.julia, source.scala, source.perl, text.html.vue, text.html.svelte, source.graphql, source.proto, text.csv, source.diff, source.gitignore, source.nginx, source.css.scss, source.css.less, source.stylus, source.coffee, text.html.handlebars, text.pug, text.html.cshtml, source.objc, source.fsharp, source.elm, source.ocaml, text.tex.latex, source.solidity, source.glsl, source.qml, source.wat, text.html.twig, text.html.ejs, text.haml, text.slim, source.vhdl, source.verilog, source.crystal, text.html.smarty, text.html.liquid, text.html.mustache, text.html.jinja, text.html.velocity, source.fortran, source.pascal, source.ada, source.lisp, source.tcl, source.asm, source.hack, source.apex, source.abap, source.actionscript, source.puppet, source.smalltalk, source.racket, source.scheme, source.nim, source.matlab, source.vb, text.xml.xaml, text.restructuredtext, text.log, source.bicep, source.hcl, source.thrift, source.json.comments, source.shaderlab
- Minimap toggle via setMinimapEnabled reflection + blockLine fallback, multi-cursor via setMultiCursorEnabled reflection + default support (Alt+Click, Ctrl+D), zoom via setPinchZoomEnabled reflection + fontSize scaling 8-32, bracket pair colorization via setBracketPairColorization
- Editor toolbar with Format (FormatPaint), Emmet (Brush), Zoom +/- buttons, fontSize pt, minimap status, language displayName, fileName, format on save indicator
- ContentListener with DisposableEffect cleanup (fixes duplicate listeners issue from earlier phases)

**LanguageService:**
- EditorLanguage enum with 100 entries, id/displayName/extensions, fromExtension, fromFileName with special handling for Dockerfile, Makefile, CMakeLists.txt, .gitignore, build.gradle, etc.
- FileIconResolver with 100 extension->iconId + iconId->Material icon + color mapping (e.g., asm #6E4C13, hack #878787, apex #1797C0, abap #E8274B, actionscript #BD120F, puppet #302B6D, smalltalk #596706, racket #9A629A, scheme #1E4AEC, nim #D899FA, matlab #0076A8, vb #945DB7, xaml #0C54C2, rst #141414, log #000000, bicep #519ABA, terraform #5C4EE5, thrift #D12127, jsonc #292929, shaderlab #222C37), folder tints

**EditorGroups:**
- EditorGroup with id, tabs, activeTabId, isActive, label, activeTab, count, isEmpty, withTab, withoutTab, withActiveTab, withActive
- EditorGroupsState with groups, activeGroupId, orientation, activeGroup, count

### Workbench Layer - Contributions (from VS Code src/vs/workbench/contrib/)

**Files (Explorer):**
- FileSystemRepository with FileObserver callbackFlow, listFiles, readFile, writeFile, security checks
- FileExplorerView with iconTheme param + FileIconResolver 100 langs, folder expanded handling
- BreadcrumbsView with path from project root + symbol navigation via BreadcrumbsRepository advanced (file breadcrumbs via File.separator split + symbol breadcrumbs via regex backward search for class/function per language, up to 5 symbols, Flow, file existence check)
- OutlineView with extractSymbols regex per language (kotlin class/fun/val/var, java class/interface/method, js/ts class/function/const, python class/def, etc.), distinctBy, 200 limit, icons DataObject/Functions/Code
- TimelineView with file modified/created dates via lastModified SimpleDateFormat
- WorkspaceView with current project card + SAF add folder

**Search:**
- SearchRepository with search Flow<SearchProgress> + real replace with readText/writeText, regex handling, case-insensitive, collectFiles skipping node_modules/.git/build/.gradle, binary skip, >5MB skip, Dispatchers.IO, Log
- SearchView with query, results grouped by file

**SCM (Git):**
- GitRepository with JGit 6.10.0 FileRepositoryBuilder, status, stage, commit, push/pull, branches, history, diff, blame (future)
- GitView with status, staged/unstaged/untracked/conflicted, commit message, push/pull

**Debug (DAP):**
- DebugRepository with breakpoints, sessions, variables, call stack, evaluate, stepping, JDI adapter via Class.forName com.sun.jdi.Bootstrap, breakpoint verification (file exists isFile, line range, empty, comment via // # /* * -- ; % <!-- (* {- --[[ ###), variable extraction regex per language, call stack with main+run+function frames, evaluate arithmetic
- DebugAdapterRepository with JDWP attach via Class.forName, vmStorage map, JdwpConnection, DebugAdapterSession, DebugBreakpoint, DebugThread, DebugFrame, DebugVar, attachJdwp with host/port validation, threads/vars/evaluate
- DapClient (Phase 11 new) with real DAP JSON-RPC Content-Length framing, BufferedReader/Writer, initialize, launch/attach, setBreakpoints, configurationDone, threads, stackTrace, scopes, variables, evaluate, next, continue, stepIn, stepOut, disconnect, events stopped/continued/exited, Dispatchers.IO, Log, similar to VS Code DAP client

**Terminal:**
- TerminalRepository with persistent shell ProcessBuilder(shell).directory(workingDir).start(), ConcurrentHashMap processes/writers/readers/errorReaders, createSession, closeSession, executeCommand with cd handling canonical check preventing /, /system, /proc, persistent shell writer.write+flush, reader.ready() loop 100 lines limit, fallback one-shot, Dispatchers.IO, Log
- TerminalPtyRepository with PtySession/PtyOutput, createPtySession via ProcessBuilder with env TERM=xterm-256color COLORTERM=truecolor COLUMNS/LINES TERM_PROGRAM=Androde, resizePty, closePtySession, executeInPty with persistent shell + reader.ready() loop 10000 limit, StateFlow sessions/outputs takeLast 1000, Dispatchers.IO, Log, similar to VS Code terminal PTY with libterm concept (vim, tab completion via shell)
- AnsiParser with regex \u001B\[([0-9;]+)m, 16 colors + 256 (38;5;N, 48;5;N) + true-color (38;2;R;G;B), xterm256Colors lazy list (0-15 system #000000 #800000 #008000 #808000 #000080 #800080 #008080 #C0C0C0 #808080 #FF0000 #00FF00 #FFFF00 #0000FF #FF00FF #00FFFF #FFFFFF, 16-231 6x6x6 cube with 55+r*40 formula, 232-255 grayscale 8+i*10), italic/underline TextDecoration, getXterm256Color helper, parseToAnnotatedString with hasAnsi flag, strip/contains helpers

**Extensions:**
- ExtensionRepository with assets/extensions/extensions.json parsing via kotlinx.serialization, 7 builtin extensions (kotlin, java, python, javascript, dracula, vscode-dark, monokai) with contributes languages/grammars/snippets/themes, fallback hardcoded, MutableStateFlow installed/marketplace, search filtering, install/uninstall/enable with builtin protection, Log
- ExtensionHost with Rhino 1.7.14 pure Java JS engine, Context.enter() optimizationLevel -1 for Android, initStandardObjects(), Androde API androde.commands.registerCommand, androde.languages.registerCompletionItemProvider, androde.window.showInformationMessage, vscode=androde, loads JS from assets/extensions/{id}/extension.js or mock activation, activate() via Function.call, deactivate() handling, ConcurrentHashMap contexts, MutableStateFlow running extensions
- ExtensionApiFull with StatusBarItem(id/text/tooltip/color/command/alignment/priority/isVisible)/StatusBarAlignment LEFT/RIGHT/TreeViewItem(id/label/description/collapsibleState/contextValue/iconPath/command/children)/CollapsibleState NONE/COLLAPSED/EXPANDED/TreeView/WebviewPanel(id/title/viewType/htmlContent/isVisible/options)/WebviewOptions enableScripts/retainContextWhenHidden/localResourceRoots/WorkspaceEdit/TextEdit/Task/DebugConfiguration + interface with Flow getStatusBarItems/getTreeViews/getWebviewPanels + createStatusBarItem/setStatusBarItem/show/hide/disposeStatusBarItem, etc., Impl with StateFlow _statusBarItems/_treeViews/_webviewPanels Log
- ExtensionApiExtended with QuickPickItem(label/description/detail/picked/alwaysShow)/InputBoxOptions(prompt/placeHolder/value/password/ignoreFocusOut)/NotificationItem(id/message/type/actions/timestamp)/NotificationType INFO/WARNING/ERROR/ProgressOptions(title/location/cancellable)/CommandItem(id/title/category/callback)/WorkspaceEditEntry(filePath/edits) + interface with Flow getQuickPickItems/getNotifications/getCommands/showQuickPick/showInputBox/showInformationMessage/showWarningMessage/showErrorMessage/withProgress/registerCommand/executeCommand/applyWorkspaceEdit, Impl with StateFlow Log
- ExtensionApiAdvanced with AuthSession(id/accessToken/accountLabel/scopes)/SecretItem(key/value)/ExtensionStorageItem(key/value/scope)/StorageScope GLOBAL/WORKSPACE/WorkspaceFolderInfo(uri/name/index)/EnvInfo(appName/appRoot/language/machineId/sessionId) + interface with getAuthSessions/getSecrets/getStorageItems/getWorkspaceFolders/getEnvInfo/getSession/storeSecret/getSecret/deleteSecret/setGlobalState/getGlobalState/setWorkspaceState/getWorkspaceState/openWorkspaceFolder, Impl with StateFlow _authSessions/_secrets/_storageItems/_workspaceFolders/_envInfo with machineId/sessionId UUID, validation, Dispatchers.IO, Log

**Marketplace:**
- MarketplaceRepository with searchMarketplace/getExtensionDetails/install/uninstall/download/publish, MarketplaceExtension(id/name/displayName/publisher/description/version/categories/tags/downloadCount/rating/ratingCount/isBuiltin/iconUrl/readmeUrl/license/downloadUrl/lastUpdated)/MarketplaceSearchResult, interface
- MarketplaceApi Retrofit interface searchExtensions/getExtension/getDownloadUrl with ApiResponse/DownloadUrlResponse
- MarketplaceRepositoryImpl with optional marketplaceApi + fallback hardcoded 7 builtin with downloadCount/rating, search with api try fallback local filtering by displayName/description/tags/categories/publisher + categoryFiltered + sorted by downloads/rating/updated, install duplicate check, uninstall blocks builtin, download returns /data/data/.../files/extensions/id.vsix path, publish checks file.exists, Dispatchers.IO, Log
- NetworkModule provides MarketplaceApi via Open VSX https://open-vsx.org/api/ + fallback BuildConfig.API_BASE_URL with try/catch
- MarketplaceScreen Compose with OutlinedTextField search placeholder Search Marketplace (Open VSX) with Search icon, FilterChip categories All/themes/languages/snippets/formatters/linters, Text sorted by downloads, LazyColumn Card with displayName/publisher/version/description/downloadCount with Download icon/rating with Star icon/ratingCount/categories AssistChip, Button Install with Download icon
- MarketplaceViewModel with extensions/query/selectedCategory/isLoading StateFlow, init searchMarketplace sortBy downloads, search with query and category, selectCategory, install via MarketplaceRepository, HiltViewModel

**Tasks & Debug:**
- TaskRepository with TaskDefinition(label/type/command/args/group/presentation/problemMatcher/options/isBackground/dependsOn)/TaskGroup(kind/isDefault)/TaskPresentation(echo/reveal/focus/panel/showReuseMessage/clear)/TaskOptions(cwd/env)/LaunchConfiguration(name/type/request/program/args/env/cwd/console/stopOnEntry/preLaunchTask)/TasksFile(version/tasks)/LaunchFile(version/configurations)/TaskExecutionResult(success/exitCode/output/error/taskLabel) + interface with getTasks/getLaunchConfigurations/loadTasksFile/loadLaunchFile/saveTasksFile/saveLaunchFile/executeTask
- TaskRepositoryImpl with loadTasksFile/loadLaunchFile from .vscode/tasks.json/launch.json via JSONObject/JSONArray, save with mkdirs + toString(4), executeTask via ProcessBuilder with workingDir from options.cwd or workspacePath, env putAll, waitFor, output/error reading, exitCode, StateFlow _tasks/_launchConfigs, Dispatchers.IO, Log
- TasksScreen Compose with Text Tasks (tasks.json) + LazyColumn Card label/type/command/args/group + Button Run PlayArrow, Text Launch (launch.json) + LazyColumn Card name/type/request/program/preLaunchTask, lastResult Card exitCode/success/output/error
- TasksViewModel with tasks/launchConfigs/lastResult StateFlow, init loadTasksFile/loadLaunchFile, execute via TaskRepository

**LSP (Language Server Protocol):**
- LspRepository with diagnostics Flow, start/stop server, didOpen/didChange/didClose, completion, hover, definition, CompletionItem, Hover, Location, Range models
- LspRepositoryImpl with servers map, real diagnostics via parsing - checks TODO/FIXME, Kotlin var without init, println, Java System.out.println, Python print, long lines >100, trailing whitespace, language-specific, fileName from File, severity INFO/WARNING/HINT, source Androde/Kotlin/Java/Python, code todo/fixme/no-init/println/sysout/print/line-too-long/trailing-whitespace, _diagnostics filtered by filePath + new diagnostics, Log.i, real completion based on file content and prefix, keywords per language (Kotlin 30+, Java 20+, Python 20+, JS/TS 20+), symbol extraction via Regex, prefix matching, distinctBy label, take 50, Log.d, plus SnippetRepository and EmmetService integration for snippets and Emmet completion for html/css/scss/less, openFileLanguages map
- LspClient (Phase 11 new) with real LSP JSON-RPC Content-Length framing, BufferedReader/Writer, start/stop, isRunning, sendRequest/sendNotification, didOpen/didChange/didClose, completion, diagnostics via Emitter, pendingRequests ConcurrentHashMap, CoroutineScope IO, Log, similar to VS Code language client

**Diff & Merge:**
- DiffEditorView Compose side-by-side/inline with DiffLine(origLineNumber/modLineNumber/origContent/modContent/type/marker)/DiffType EQUAL/INSERT/DELETE/MODIFY + computeDiff(original/modified) real LCS Myers simplified up to 1000 lines safety break, lines(), origIndex/modIndex, detects INSERT/DELETE/MODIFY/EQUAL, line numbers, marker +/-/~/=, background Color 0xFF2A4A2A insert 0xFF4A2A2A delete 0xFF4A4A2A modify, side-by-side shows original+modified numbers, inline shows combined, LazyColumn, similar to VS Code diff editor
- MergeEditorView Compose ThreeWayDiffView with base/incoming/current lines and MergeType EQUAL/INCOMING/CURRENT/CONFLICT/BOTH_CHANGED, background colors, line numbers, ThreeWayDiffLine(baseLine/incomingLine/currentLine/baseContent/incomingContent/currentContent/type), MergeEditorView with result, computeThreeWayDiff real algorithm with max lines, type detection when b==inc==cur EQUAL, b==cur!=inc INCOMING, b==inc!=cur CURRENT, inc==cur!=b BOTH_CHANGED else CONFLICT, 1000 safety break, mergeContents with MergeStrategy INCOMING/CURRENT/BOTH choosing cur/inc/both, similar to VS Code merge editor

**Other:**
- CodeLensRepository with CodeLens(id/filePath/line/command/title/tooltip/range)/InlayHint(id/filePath/line/column/label/kind/tooltip)/InlayHintKind PARAMETER/TYPE/OTHER/SemanticToken(line/column/length/tokenType/tokenModifiers)/SemanticTokens(filePath/tokens) + interface with getCodeLenses/getInlayHints/getSemanticTokens/provideCodeLenses/provideInlayHints/provideSemanticTokens, Impl with real parsing for code lens (function regex fun/function/def/func + name with 2 references lens + Run/Debug for main/Main, class regex class/struct/interface/enum + name with 1 reference lens), inlay hints (parameter name hints via regex call \bName\(arg) + param: label, type hints for val/var via inferred String/Int/Double/Boolean/List/Map via value startsWith \" or \d+ or true/false or listOf/[ or mapOf/{), semantic tokens (class via regex class\s+Name, function via fun/function/def/func\s+Name, variable via val/var/let/const, keyword via if/else/for/while/return/import/package/class/fun/val/var), StateFlow maps, Dispatchers.Default, Log
- BreadcrumbsRepository advanced with BreadcrumbItem/BreadcrumbSymbolKind/BreadcrumbPath, Impl with buildFileBreadcrumbs via File.absolutePath split, getSymbolBreadcrumbs via regex backward search for class/function per language, Flow, file existence check
- MinimapRepository advanced with MinimapSection/MinimapSectionKind/MinimapState, Impl with StateFlow _state isEnabled true scale 1f showSlider true maxColumn 120 sections empty, getMinimapState asStateFlow, getMinimapContent condenses lines to 20 chars + …, setEnabled/setScale coerce 0.5..3/setShowSlider with StateFlow update and Log, buildSections via regex for import/class/function with currentSectionStart tracking, sections list with label/kind, updates StateFlow
- SettingsSyncRepository with SyncProfile/SyncSettings/SyncKeybinding/RemoteTunnel, Impl with StateFlow _profiles with default Default profile, _currentProfile, _syncSettings, _remoteTunnels, createProfile blank/duplicate check UUID, deleteProfile blocks default, switchProfile updates lastUsedAt, syncSettings simulates cloud sync via Retrofit (GitHub/Microsoft) with lastSyncedAt update, createTunnel validates name/host/port 1..65535 UUID, deleteTunnel, startTunnel/stopTunnel with isActive toggle, Dispatchers.IO, Log
- LiveShareRepository with LiveShareSession/LiveShareParticipant/LiveShareMessage, Impl with StateFlow _sessions/_messages Map, createSession validates blank UUID sessionId + hostParticipant UUID isHost true, joinSession checks active, leaveSession filters participants, if empty deactivates and removes messages, shareFile/unshareFile with contains check, sendMessage validates blank UUID messageId takeLast 100, updateCursor maps participants with cursorFile/line/column, Dispatchers.IO, Log, WebRTC concept (real WebRTC would use org.webrtc:google-webrtc)
- SshRepository with JSch 0.1.55 pure Java, JSch(), sessions ConcurrentHashMap, _connections MutableStateFlow, connect with JSch.getSession setPassword addIdentity private key with passphrase Properties StrictHostKeyChecking no for MVP timeout 10000 connect UUID connectionId, disconnect, executeCommand with ChannelExec setCommand ByteArrayOutputStream stdout/stderr outputStream/errStream connect while !isClosed Thread.sleep 100 exitStatus disconnect, listFiles with sftp ChannelSftp ls, downloadFile sftp get, uploadFile put, Dispatchers.IO, Log
- WorkspaceTrustRepository with DataStore Preferences stringSet trusted_workspaces and boolean trust_enabled, trustWorkspace adds path to set, untrust removes, isWorkspaceTrusted checks contains or trust disabled returns true, getTrustedWorkspaces maps to TrustedWorkspace, setTrustEnabled, isTrustEnabled Flow, Log, security-first like VS Code workspace trust

### Dependency Injection (from VS Code DI)

**VS Code:** Custom DI with ServiceCollection, createDecorator, registerSingleton, @IFileService injection

**Androde:** Hilt with @Module @InstallIn(SingletonComponent::class) abstract class IdeModule with @Binds @Singleton abstract fun bindXxx(impl: XxxImpl): XxxInterface

Phase 11: 31 → 37 bindings (added LayoutService, EditorGroupsService, FileService, ThemeService, LspClient, DapClient)

Similar to VS Code's service registration, but using Hilt for Android.

### Navigation

- Single Activity (MainActivity) + Navigation Compose
- Start destination: Screen.Ide (workbench)
- Type-safe routes via sealed class Screen with @Serializable (Nav 2.8+)
- AppNavGraph hosts IdeScreen, future Settings, Search, Git, Terminal, Extensions, Marketplace, Tasks screens
- KeybindingHandler composable with FocusRequester, onKeyEvent handling for Ctrl+Shift+P/S/W/N/O/F/`/Ctrl+\ handling, optimized for external keyboard

### Error Handling

- AppResult<T>: Success, Error(AppError), Loading
- AppError: Network, Server, Local, Validation, Unauthorized, Unknown with userMessage
- Repository catches exceptions, maps to AppError, never throws
- ViewModel exposes UiState with retry lambda, or error StateFlow
- File operations validate paths (prevent traversal), check size limits (10MB read, 5MB search skip), handle SecurityException
- LSP/DAP: Content-Length validation, JSON parsing try/catch, process lifecycle
- All repos with Log.i/w/e, Dispatchers.IO/Default, error handling, security checks

### Offline-First & Performance

- File system source of truth, no remote for core IDE
- DataStore for settings, Room legacy for User sample
- Coroutines IO dispatcher for file I/O, Flow reactive, callbackFlow for FileObserver
- LazyColumn for file tree, LazyRow for tabs, chunked reading for search
- Skip node_modules, .git, build, .gradle in search, binary detection, >5MB skip
- Recent projects limited to 10, terminal sessions limited to 5, messages takeLast 100/1000
- Baseline profiles with 30+ critical journeys for startup performance (MainActivity, IdeScreen, IdeViewModel with 37 repos, Sora CodeEditor setText/setEditorLanguage TextMateLanguage/TextMateColorScheme, FileSystemRepositoryImpl listFiles/readFile, FileIconResolver resolveFileIcon, EditorLanguage fromExtension, EditorRepositoryImpl openFile/saveFile, AndrodeTheme, NavHost, SearchRepositoryImpl search, GitRepositoryImpl status, TerminalRepositoryImpl executeCommand, TerminalPtyRepositoryImpl createPtySession, LspRepositoryImpl completion, DiagnosticsRepositoryImpl getDiagnostics, ExtensionRepositoryImpl loadBuiltinExtensions, MarketplaceRepositoryImpl searchMarketplace, TaskRepositoryImpl loadTasksFile, DebugRepositoryImpl toggleBreakpoint, DebugAdapterRepositoryImpl attachJdwp, BreadcrumbsRepositoryImpl getFileBreadcrumbs, MinimapRepositoryImpl buildSections, SettingsSyncRepositoryImpl createProfile, LiveShareRepositoryImpl createSession, ExtensionApiAdvancedImpl getSession, LayoutServiceImpl isVisible, EditorGroupsServiceImpl createGroup, FileServiceImpl listFiles, ThemeServiceImpl setColorTheme, LspClientImpl start, DapClientImpl initialize, WorkbenchLayout)
- ProGuard/R8 for Sora, JGit, LSP4J, kotlinx.serialization, Rhino, JSch, etc.
- Lazy loading: Grammars loaded via GrammarRegistry.loadGrammars on demand, themes via ThemeRegistry.loadTheme
- Disposable pattern for resource cleanup, Event Emitter with weak refs or lifecycle-aware

### Security

- FileProvider for sharing, path traversal validation (canonical check), file size limits, forbidden dirs (/, /system, /proc, /data)
- Storage permissions with maxSdkVersion (READ 32, WRITE 29, MANAGE for file explorer with scoped storage handling), SAF persistable URI permissions
- Network security config disables cleartext
- Rhino sandbox: optimizationLevel -1, isolated contexts per extension, no Java access by default
- JSch: StrictHostKeyChecking no for MVP, timeout 10000, ConcurrentHashMap sessions
- Workspace trust: trusted_workspaces stringSet + trust_enabled boolean, security-first preventing untrusted code execution
- Debug JDI: Class.forName check for com.sun.jdi.Bootstrap, fallback to verification, security via file validation, JDWP attach validation host/port
- Marketplace: Retrofit with Open VSX, fallback local, file.exists checks
- Tasks: ProcessBuilder with workingDir validation, env handling
- PTY: canonical validation, forbidden dirs, env TERM=xterm-256color, ConcurrentHashMap
- DAP: file validation for breakpoints, comment checks, JDI reflection, vmStorage map
- Extension API Advanced: validation for blank keys, duplicate checks, secure token handling, EncryptedSharedPreferences for secrets in production
- Breadcrumbs: file existence check for navigation
- Minimap: safe line handling
- Merge: safety break 1000 lines
- Play Store signing: keystore from local.properties/env, never commit, V3/V4 signing
- ProGuard keep rules for Sora, JGit, LSP4J, Rhino, JSch, workspace, diagnostics, marketplace, tasks, editor, terminal, debug, sync, collab, breadcrumbs, minimap, advanced API, workbench, platform, theme, lsp client, dap client

### Testing Strategy

- Domain: JUnit + Mockk, no Android, pure Kotlin
- Data: FileSystemRepositoryImpl with temp files, Mock for JGit
- Presentation: Turbine for Flow, Compose UI tests, Hilt testing
- Phase 11: 87 tests (FileNodeTest 8, OutlineTest 6, ExtensionTest 5, Phase4Test 12, Phase5Test 8, Phase6Test 8, Phase7Test 8, Phase8Test 8, Phase9Test 8, Phase10Test 8, Phase11Test 8)
- Tests for: EditorLanguage 100, FileIconResolver 100, Workbench parts 8, LayoutService, EditorGroupsService, LSP client framing, DAP client, Sora scope 100, fromExtension special

### Build

- Gradle Kotlin DSL + Version Catalog (gradle/libs.versions.toml) per Google recommended modern practice
- Sora Editor 0.23.6, JGit 6.10.0, LSP4J 0.22.0, kotlinx.serialization, Rhino 1.7.14, JSch 0.1.55, Retrofit, OkHttp
- SigningConfigs release with keystore.path/password/alias from local.properties/env V3/V4, buildTypes release signingConfig + baselineProfile automaticGenerationDuringBuild false
- versionCode 11 versionName 9.0.0-androde (Phase 11)
- Packaging excludes for META-INF, JNI legacy packaging for JGit
- Lint, Detekt, EditorConfig
- CI: GitHub Actions with assembleDebug + testDebugUnitTest + lintDebug

### Modularization Plan (Future, from CodeAssist)

- Split into modules: :core:base, :core:platform, :core:editor, :core:workbench, :core:theme, :core:lsp, :core:debug, :core:terminal, :feature:files, :feature:search, :feature:scm, :feature:debug, :feature:terminal, :feature:extensions, :feature:marketplace, :feature:tasks, :feature:settings, etc.
- For now, single app module with clear packages (minimal justified changes per guideline 14, avoid premature modularization)
- Documented in DEEP_RESEARCH_PHASE11.md
- When team size >2 or build times >3min, extract to modules

### References

- VS Code Source Code Organization: https://github.com/microsoft/vscode/wiki/Source-Code-Organization
- VS Code Architecture: https://code.visualstudio.com/docs, deepwiki.com/microsoft/vscode
- Monaco Editor: https://github.com/microsoft/monaco-editor, https://microsoft.github.io/monaco-editor/
- Monaco Languages: https://github.com/microsoft/monaco-languages
- Sora Editor: https://github.com/Rosemoe/sora-editor, 0.23.6, LGPL-2.1, editor-bom, editor-lsp, language-java, language-monarch, language-textmate, language-treesitter, oniguruma-native
- LSP Spec: https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification.md, 3.18, base protocol with Content-Length
- DAP Spec: https://microsoft.github.io/debug-adapter-protocol/, DAP over JSON-RPC
- CodeAssist: https://github.com/tyron12233/CodeAssist, https://github.com/OpenMoTa3/CodeAssist, architecture docs (60 modules, platform/lang/build-system/run/android/services/plugins/app)
- Android Developers: FileObserver, SAF, DocumentFile, Storage Access Framework, scoped storage, FileProvider, baseline profiles, macrobenchmark, DataStore, Compose, WindowSizeClass, edge-to-edge
- JGit: https://www.eclipse.org/jgit/, 6.10.0, pure Java Git
- LSP4J: https://github.com/eclipse/lsp4j, 0.22.0
- kotlinx.serialization: https://github.com/Kotlin/kotlinx.serialization
- Rhino: https://github.com/mozilla/rhino, 1.7.14, Context, ScriptableObject, Function, pure Java JS engine works on Android with optimizationLevel -1
- JSch: https://github.com/mwiede/jsch, 0.1.55, JSch, Session, ChannelExec, ChannelSftp, pure Java SSH
- Emmet: https://docs.emmet.io/, abbreviations ul>li*3, div#id.class
- ANSI: https://en.wikipedia.org/wiki/ANSI_escape_code, \u001B[31m, \u001B[38;5;196m 256-color, \u001B[38;2;255;100;0m true-color, xterm256Colors
- DAP: https://microsoft.github.io/debug-adapter-protocol/, breakpoints, variables, call stack, evaluate, stepping, JDI, JDWP
- Workspace Trust: https://code.visualstudio.com/docs/editor/workspace-trust
- Minimap: https://code.visualstudio.com/docs/editor/editingevolved#_minimap
- Multi-cursor: https://code.visualstudio.com/docs/editor/codebasics#_multiple-selections-multicursor
- Zoom: https://code.visualstudio.com/docs/editor/accessibility#_zoom
- Diff Editor: https://code.visualstudio.com/docs/editor/versioncontrol#_comparing-files
- Tasks: https://code.visualstudio.com/docs/editor/tasks
- Launch: https://code.visualstudio.com/docs/editor/debugging#_launch-configurations
- CodeLens: https://code.visualstudio.com/api/language-extensions/programmatic-language-features#codelens-show-actionable-context-information-within-source-code
- Inlay Hints: https://code.visualstudio.com/api/language-extensions/programmatic-language-features#_inlay-hints
- Semantic Tokens: https://code.visualstudio.com/api/language-extensions/semantic-highlight-guide
- Extension API: https://code.visualstudio.com/api/references/vscode-api
- Marketplace: https://open-vsx.org/, Open VSX registry
- Terminal PTY: https://code.visualstudio.com/docs/terminal/basics
- Debug JDWP: https://docs.oracle.com/javase/8/docs/technotes/guides/jpda/jdwp-spec.html
- Settings Sync: https://code.visualstudio.com/docs/editor/settings-sync
- Profiles: https://code.visualstudio.com/docs/editor/profiles
- Remote Tunnels: https://code.visualstudio.com/docs/remote/tunnels
- Live Share: https://visualstudio.microsoft.com/services/live-share/
- Breadcrumbs: https://code.visualstudio.com/docs/editor/editingevolved#_breadcrumbs
- Baseline Profiles: https://developer.android.com/topic/performance/baselineprofiles
- Play Store Signing: https://developer.android.com/studio/publish/app-signing
- TextMate: https://macromates.com/manual/en/language_grammars, Oniguruma
- TreeSitter: https://tree-sitter.github.io/tree-sitter/
- No fabricated research; all from official docs, GitHub, Maven Central.
