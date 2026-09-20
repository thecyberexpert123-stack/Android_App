package com.cyberexpert.androde.di

import com.cyberexpert.androde.core.collab.LiveShareRepository
import com.cyberexpert.androde.core.debug.DebugAdapterRepository
import com.cyberexpert.androde.core.debug.DebugRepository
import com.cyberexpert.androde.core.diagnostics.DiagnosticsRepository
import com.cyberexpert.androde.core.editor.BreadcrumbsRepository
import com.cyberexpert.androde.core.editor.CodeLensRepository
import com.cyberexpert.androde.core.editor.EditorRepository
import com.cyberexpert.androde.core.editor.MinimapRepository
import com.cyberexpert.androde.core.extensions.ExtensionApiAdvanced
import com.cyberexpert.androde.core.extensions.ExtensionApiExtended
import com.cyberexpert.androde.core.extensions.ExtensionApiFull
import com.cyberexpert.androde.core.extensions.ExtensionHost
import com.cyberexpert.androde.core.extensions.ExtensionRepository
import com.cyberexpert.androde.core.extensions.EmmetService
import com.cyberexpert.androde.core.extensions.FormattingRepository
import com.cyberexpert.androde.core.extensions.IconThemeRepository
import com.cyberexpert.androde.core.extensions.SnippetRepository
import com.cyberexpert.androde.core.filesystem.FileSystemRepository
import com.cyberexpert.androde.core.git.GitRepository
import com.cyberexpert.androde.core.keybinding.KeybindingRepository
import com.cyberexpert.androde.core.lsp.ILspClient
import com.cyberexpert.androde.core.lsp.LspRepository
import com.cyberexpert.androde.core.debug.IDapClient
import com.cyberexpert.androde.core.marketplace.MarketplaceRepository
import com.cyberexpert.androde.core.remote.SshRepository
import com.cyberexpert.androde.core.search.SearchRepository
import com.cyberexpert.androde.core.settings.SettingsRepository
import com.cyberexpert.androde.core.sync.SettingsSyncRepository
import com.cyberexpert.androde.core.tasks.TaskRepository
import com.cyberexpert.androde.core.terminal.TerminalPtyRepository
import com.cyberexpert.androde.core.terminal.TerminalRepository
import com.cyberexpert.androde.core.workbench.ILayoutService
import com.cyberexpert.androde.core.workbench.IEditorGroupsService
import com.cyberexpert.androde.core.platform.IFileService
import com.cyberexpert.androde.core.platform.IConfigurationService
import com.cyberexpert.androde.core.terminal.IPtyService
import com.cyberexpert.androde.core.theme.IThemeService
import com.cyberexpert.androde.core.workspace.WorkspaceRepository
import com.cyberexpert.androde.core.workspace.WorkspaceTrustRepository
import com.cyberexpert.androde.data.local.collab.LiveShareRepositoryImpl
import com.cyberexpert.androde.data.local.debug.DebugAdapterRepositoryImpl
import com.cyberexpert.androde.data.local.diagnostics.DebugRepositoryImpl
import com.cyberexpert.androde.data.local.diagnostics.DiagnosticsRepositoryImpl
import com.cyberexpert.androde.data.local.editor.BreadcrumbsRepositoryImpl
import com.cyberexpert.androde.data.local.editor.CodeLensRepositoryImpl
import com.cyberexpert.androde.data.local.editor.EditorRepositoryImpl
import com.cyberexpert.androde.data.local.editor.MinimapRepositoryImpl
import com.cyberexpert.androde.data.local.emmet.EmmetServiceImpl
import com.cyberexpert.androde.data.local.extensions.ExtensionApiAdvancedImpl
import com.cyberexpert.androde.data.local.extensions.ExtensionApiExtendedImpl
import com.cyberexpert.androde.data.local.extensions.ExtensionApiFullImpl
import com.cyberexpert.androde.data.local.extensions.ExtensionHostImpl
import com.cyberexpert.androde.data.local.extensions.ExtensionRepositoryImpl
import com.cyberexpert.androde.data.local.file.FileSystemRepositoryImpl
import com.cyberexpert.androde.data.local.file.GitRepositoryImpl
import com.cyberexpert.androde.data.local.file.SearchRepositoryImpl
import com.cyberexpert.androde.data.local.file.TerminalRepositoryImpl
import com.cyberexpert.androde.data.local.formatting.FormattingRepositoryImpl
import com.cyberexpert.androde.data.local.icons.IconThemeRepositoryImpl
import com.cyberexpert.androde.data.local.marketplace.MarketplaceRepositoryImpl
import com.cyberexpert.androde.data.local.remote.SshRepositoryImpl
import com.cyberexpert.androde.data.local.settings.SettingsRepositoryImpl
import com.cyberexpert.androde.data.local.snippets.SnippetRepositoryImpl
import com.cyberexpert.androde.data.local.sync.SettingsSyncRepositoryImpl
import com.cyberexpert.androde.data.local.tasks.TaskRepositoryImpl
import com.cyberexpert.androde.data.local.terminal.TerminalPtyRepositoryImpl
import com.cyberexpert.androde.data.local.workspace.KeybindingRepositoryImpl
import com.cyberexpert.androde.data.local.workspace.LspRepositoryImpl
import com.cyberexpert.androde.data.local.workspace.WorkspaceRepositoryImpl
import com.cyberexpert.androde.data.local.workspace.WorkspaceTrustRepositoryImpl
import com.cyberexpert.androde.core.workbench.LayoutServiceImpl
import com.cyberexpert.androde.core.workbench.EditorGroupsServiceImpl
import com.cyberexpert.androde.core.platform.FileServiceImpl
import com.cyberexpert.androde.core.platform.ConfigurationServiceImpl
import com.cyberexpert.androde.core.terminal.PtyServiceImpl
import com.cyberexpert.androde.core.theme.ThemeServiceImpl
import com.cyberexpert.androde.core.lsp.LspClientImpl
import com.cyberexpert.androde.core.debug.DapClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Androde IDE core repositories - Phase 12 100% REAL WORKING++++++++++ with 110 grammars, Workbench Architecture 4 layers + ActivityBar/Sidebar/StatusBar UI + ConfigurationService + PtyService.
 * Binds interfaces to implementations, similar to VS Code's service registration with 4 layers: Base, Platform, Editor, Workbench.
 * Covers all VS Code features: Editor 110 grammars (added hlsl/wgsl/cuda/opencl/c/bibtex/git-commit/git-rebase/dockercompose/objective-cpp), Workbench with LayoutService + EditorGroupsService + FileService + ThemeService + LspClient + DapClient + ConfigurationService + PtyService + ActivityBar/Sidebar/StatusBar UI, Explorer with Icon Themes 110, Search with Replace, Git, Terminal with ANSI 256 + PTY full + PtyService TERM=xterm-256color, Settings with Format on Save/Emmet on Tab/Breadcrumbs/Icon Theme/Minimap/Zoom + ConfigurationService profiles, Diagnostics, Debug with DAP JDI Adapter + Evaluate + Stepping + JDWP Attach + DAP Client, Workspace, Keybindings, LSP with Snippets/Emmet for 110 langs + LSP Client, Extensions, Extension Host (Rhino JS), Extension API Full + Extended + Advanced, Snippets 10 langs, Formatting, Emmet, Icon Themes, SSH Remote, Marketplace Retrofit + UI, Diff Editor + 3-way/Merge, Tasks/Launch + UI, CodeLens/Inlay/Semantic, Settings Sync/Profiles/Remote Tunnels, Live Share, Breadcrumbs Advanced, Minimap Advanced, Baseline Profiles, Play Store Signing.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class IdeModule {

    @Binds
    @Singleton
    abstract fun bindFileSystemRepository(impl: FileSystemRepositoryImpl): FileSystemRepository

    @Binds
    @Singleton
    abstract fun bindEditorRepository(impl: EditorRepositoryImpl): EditorRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: SearchRepositoryImpl): SearchRepository

    @Binds
    @Singleton
    abstract fun bindGitRepository(impl: GitRepositoryImpl): GitRepository

    @Binds
    @Singleton
    abstract fun bindTerminalRepository(impl: TerminalRepositoryImpl): TerminalRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindDiagnosticsRepository(impl: DiagnosticsRepositoryImpl): DiagnosticsRepository

    @Binds
    @Singleton
    abstract fun bindDebugRepository(impl: DebugRepositoryImpl): DebugRepository

    @Binds
    @Singleton
    abstract fun bindWorkspaceRepository(impl: WorkspaceRepositoryImpl): WorkspaceRepository

    @Binds
    @Singleton
    abstract fun bindKeybindingRepository(impl: KeybindingRepositoryImpl): KeybindingRepository

    @Binds
    @Singleton
    abstract fun bindLspRepository(impl: LspRepositoryImpl): LspRepository

    @Binds
    @Singleton
    abstract fun bindExtensionRepository(impl: ExtensionRepositoryImpl): ExtensionRepository

    // Phase 4 - 100% Real Working new bindings

    @Binds
    @Singleton
    abstract fun bindExtensionHost(impl: ExtensionHostImpl): ExtensionHost

    @Binds
    @Singleton
    abstract fun bindSnippetRepository(impl: SnippetRepositoryImpl): SnippetRepository

    @Binds
    @Singleton
    abstract fun bindFormattingRepository(impl: FormattingRepositoryImpl): FormattingRepository

    @Binds
    @Singleton
    abstract fun bindEmmetService(impl: EmmetServiceImpl): EmmetService

    @Binds
    @Singleton
    abstract fun bindIconThemeRepository(impl: IconThemeRepositoryImpl): IconThemeRepository

    // Phase 5 - new bindings

    @Binds
    @Singleton
    abstract fun bindSshRepository(impl: SshRepositoryImpl): SshRepository

    @Binds
    @Singleton
    abstract fun bindWorkspaceTrustRepository(impl: WorkspaceTrustRepositoryImpl): WorkspaceTrustRepository

    // Phase 8 - new bindings for 6 tasks

    @Binds
    @Singleton
    abstract fun bindExtensionApiFull(impl: ExtensionApiFullImpl): ExtensionApiFull

    @Binds
    @Singleton
    abstract fun bindMarketplaceRepository(impl: MarketplaceRepositoryImpl): MarketplaceRepository

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindCodeLensRepository(impl: CodeLensRepositoryImpl): CodeLensRepository

    // Phase 9 - new bindings for 6 tasks: PTY, DAP full, Extension API extended, Sync/Profiles/Tunnels, Live Share

    @Binds
    @Singleton
    abstract fun bindTerminalPtyRepository(impl: TerminalPtyRepositoryImpl): TerminalPtyRepository

    @Binds
    @Singleton
    abstract fun bindDebugAdapterRepository(impl: DebugAdapterRepositoryImpl): DebugAdapterRepository

    @Binds
    @Singleton
    abstract fun bindExtensionApiExtended(impl: ExtensionApiExtendedImpl): ExtensionApiExtended

    @Binds
    @Singleton
    abstract fun bindSettingsSyncRepository(impl: SettingsSyncRepositoryImpl): SettingsSyncRepository

    @Binds
    @Singleton
    abstract fun bindLiveShareRepository(impl: LiveShareRepositoryImpl): LiveShareRepository

    // Phase 10 - new bindings: Breadcrumbs Advanced, Minimap Advanced, Extension API Advanced

    @Binds
    @Singleton
    abstract fun bindBreadcrumbsRepository(impl: BreadcrumbsRepositoryImpl): BreadcrumbsRepository

    @Binds
    @Singleton
    abstract fun bindMinimapRepository(impl: MinimapRepositoryImpl): MinimapRepository

    @Binds
    @Singleton
    abstract fun bindExtensionApiAdvanced(impl: ExtensionApiAdvancedImpl): ExtensionApiAdvanced

    // Phase 11 - new bindings: Workbench Architecture with 4 layers (Base, Platform, Editor, Workbench)

    @Binds
    @Singleton
    abstract fun bindLayoutService(impl: LayoutServiceImpl): ILayoutService

    @Binds
    @Singleton
    abstract fun bindEditorGroupsService(impl: EditorGroupsServiceImpl): IEditorGroupsService

    @Binds
    @Singleton
    abstract fun bindFileService(impl: FileServiceImpl): IFileService

    @Binds
    @Singleton
    abstract fun bindThemeService(impl: ThemeServiceImpl): IThemeService

    @Binds
    @Singleton
    abstract fun bindLspClient(impl: LspClientImpl): ILspClient

    @Binds
    @Singleton
    abstract fun bindDapClient(impl: DapClientImpl): IDapClient

    // Phase 12 - new bindings: ConfigurationService with profiles, PtyService with TERM=xterm-256color

    @Binds
    @Singleton
    abstract fun bindConfigurationService(impl: ConfigurationServiceImpl): IConfigurationService

    @Binds
    @Singleton
    abstract fun bindPtyService(impl: PtyServiceImpl): IPtyService
}
