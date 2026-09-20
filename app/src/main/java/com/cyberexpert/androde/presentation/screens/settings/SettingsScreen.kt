package com.cyberexpert.androde.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.core.settings.AppTheme

/**
 * Settings screen, similar to VS Code Preferences - Phase 5 with format on save, emmet on tab, breadcrumbs, icon themes.
 * Features:
 * - Theme selection (VS Code Dark+, Light, etc.)
 * - Font size, tab size
 * - Word wrap, minimap, auto save
 * - Format on save, Emmet on Tab, Breadcrumbs, Icon Theme
 * - File explorer settings
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val theme by viewModel.theme.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val tabSize by viewModel.tabSize.collectAsState()
    val wordWrap by viewModel.wordWrap.collectAsState()
    val minimap by viewModel.minimap.collectAsState()
    val autoSave by viewModel.autoSave.collectAsState()
    val showHidden by viewModel.showHidden.collectAsState()
    val formatOnSave by viewModel.formatOnSave.collectAsState()
    val emmetOnTab by viewModel.emmetOnTab.collectAsState()
    val breadcrumbsEnabled by viewModel.breadcrumbsEnabled.collectAsState()
    val minimapEnabled by viewModel.minimapEnabled.collectAsState()
    val iconThemeId by viewModel.iconThemeId.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Settings - Phase 5") })

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Appearance", style = MaterialTheme.typography.titleMedium)
                SettingItem(
                    title = "Theme",
                    description = "Color theme, similar to VS Code themes"
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(theme.name)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        AppTheme.entries.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t.name) },
                                onClick = {
                                    viewModel.setTheme(t)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                SettingItem(
                    title = "Icon Theme",
                    description = "File icon theme: $iconThemeId"
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    OutlinedButton(onClick = { expanded = true }) {
                        Text(iconThemeId)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        listOf("vscode_icons", "material_icons").forEach { id ->
                            DropdownMenuItem(
                                text = { Text(id) },
                                onClick = {
                                    viewModel.setIconThemeId(id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                SettingSlider(
                    title = "Font Size",
                    value = fontSize.toFloat(),
                    range = 8f..32f,
                    onValueChange = { viewModel.setFontSize(it.toInt()) }
                )

                SettingToggle(
                    title = "Word Wrap",
                    description = "Wrap long lines",
                    checked = wordWrap,
                    onCheckedChange = { viewModel.setWordWrap(it) }
                )

                SettingToggle(
                    title = "Minimap",
                    description = "Show minimap in editor (legacy)",
                    checked = minimap,
                    onCheckedChange = { viewModel.setMinimap(it) }
                )

                SettingToggle(
                    title = "Minimap Enabled",
                    description = "Show minimap - Phase 5",
                    checked = minimapEnabled,
                    onCheckedChange = { viewModel.setMinimapEnabled(it) }
                )

                SettingToggle(
                    title = "Breadcrumbs",
                    description = "Show breadcrumbs navigation",
                    checked = breadcrumbsEnabled,
                    onCheckedChange = { viewModel.setBreadcrumbsEnabled(it) }
                )
            }

            item {
                Text("Editor", style = MaterialTheme.typography.titleMedium)
                SettingSlider(
                    title = "Tab Size",
                    value = tabSize.toFloat(),
                    range = 1f..8f,
                    onValueChange = { viewModel.setTabSize(it.toInt()) }
                )
                SettingToggle(
                    title = "Auto Save",
                    description = "Automatically save files",
                    checked = autoSave,
                    onCheckedChange = { viewModel.setAutoSave(it) }
                )
                SettingToggle(
                    title = "Format On Save",
                    description = "Automatically format file on save (Shift+Alt+F logic) - Phase 5",
                    checked = formatOnSave,
                    onCheckedChange = { viewModel.setFormatOnSave(it) }
                )
                SettingToggle(
                    title = "Emmet On Tab",
                    description = "Expand Emmet abbreviation on Tab key - Phase 5",
                    checked = emmetOnTab,
                    onCheckedChange = { viewModel.setEmmetOnTab(it) }
                )
            }

            item {
                Text("Explorer", style = MaterialTheme.typography.titleMedium)
                SettingToggle(
                    title = "Show Hidden Files",
                    description = "Show hidden files and folders",
                    checked = showHidden,
                    onCheckedChange = { viewModel.setShowHidden(it) }
                )
            }

            item {
                Text("Phase 5 Features", style = MaterialTheme.typography.titleMedium)
                Text(
                    "32 grammars (toml, groovy, lua, r, bat, powershell, makefile, cmake added), icon themes in explorer with FileIconResolver, snippets completion in LSP, Emmet Tab expansion, format on save, breadcrumbs toggle, minimap toggle, icon theme selection",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingItem(
    title: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            if (description != null) {
                Text(description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        content()
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingItem(title = title, description = description) {
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingSlider(
    title: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text("${value.toInt()}", style = MaterialTheme.typography.labelMedium)
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}
