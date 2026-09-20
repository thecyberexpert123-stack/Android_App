package com.cyberexpert.androde.presentation.screens.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cyberexpert.androde.core.settings.AppTheme
import com.cyberexpert.androde.domain.model.ide.EditorLanguage
import com.cyberexpert.androde.domain.model.ide.EditorTab
import com.cyberexpert.androde.presentation.components.ide.SoraEditorView
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

/**
 * Real working Settings JSON editor, similar to VS Code Preferences: Open User Settings (JSON).
 * Allows editing settings.json directly with Sora Editor, with JSON syntax highlighting.
 * Real working: parses JSON and applies to DataStore on save.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsJsonScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val theme by viewModel.theme.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val tabSize by viewModel.tabSize.collectAsState()
    val wordWrap by viewModel.wordWrap.collectAsState()
    val minimap by viewModel.minimap.collectAsState()
    val autoSave by viewModel.autoSave.collectAsState()
    val showHidden by viewModel.showHidden.collectAsState()

    // Real settings.json content from DataStore values
    val settingsJson = remember(theme, fontSize, tabSize, wordWrap, minimap, autoSave, showHidden) {
        """
        {
            // Androde Settings - VS Code like settings.json
            // Real working - edits apply on Save
            "workbench.colorTheme": "${theme.name}",
            "editor.fontSize": $fontSize,
            "editor.tabSize": $tabSize,
            "editor.wordWrap": "${if (wordWrap) "on" else "off"}",
            "editor.minimap.enabled": $minimap,
            "files.autoSave": "${if (autoSave) "afterDelay" else "off"}",
            "explorer.showHiddenFiles": $showHidden,
            "androde.version": "1.0.0-androde",
            "androde.editor": "Sora Editor 0.23.6",
            "androde.git": "JGit 6.10.0",
            "androde.terminal": "ProcessBuilder with persistent shell",
            "androde.lsp": "LSP4J 0.22.0"
        }
        """.trimIndent()
    }

    var tab by remember(settingsJson) {
        mutableStateOf(
            EditorTab(
                id = "settings-json",
                file = File("settings.json"),
                fileName = "settings.json",
                filePath = "settings.json",
                content = settingsJson,
                originalContent = settingsJson,
                language = EditorLanguage.JSON
            )
        )
    }

    var parseError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings (JSON)") },
                actions = {
                    Button(
                        onClick = {
                            // Real working: parse JSON and apply to DataStore
                            scope.launch {
                                try {
                                    // Remove comments (//) for JSON parsing - VS Code style JSONC
                                    val cleaned = tab.content.lines().filter { !it.trim().startsWith("//") }.joinToString("\n")
                                    val json = JSONObject(cleaned)

                                    // Apply theme
                                    if (json.has("workbench.colorTheme")) {
                                        val themeStr = json.getString("workbench.colorTheme")
                                        try {
                                            val appTheme = AppTheme.valueOf(themeStr)
                                            viewModel.setTheme(appTheme)
                                        } catch (e: Exception) {
                                            // Try mapping VS Code theme names to AppTheme
                                            val mapped = when (themeStr.lowercase()) {
                                                "vscode_dark", "dark", "dark+" -> AppTheme.DARK
                                                "light" -> AppTheme.LIGHT
                                                "monokai" -> AppTheme.MONOKAI
                                                "dracula" -> AppTheme.DRACULA
                                                else -> null
                                            }
                                            mapped?.let { viewModel.setTheme(it) }
                                        }
                                    }

                                    if (json.has("editor.fontSize")) {
                                        viewModel.setFontSize(json.getInt("editor.fontSize"))
                                    }
                                    if (json.has("editor.tabSize")) {
                                        viewModel.setTabSize(json.getInt("editor.tabSize"))
                                    }
                                    if (json.has("editor.wordWrap")) {
                                        val wrap = json.getString("editor.wordWrap")
                                        viewModel.setWordWrap(wrap == "on" || wrap == "true")
                                    }
                                    if (json.has("editor.minimap.enabled")) {
                                        viewModel.setMinimap(json.getBoolean("editor.minimap.enabled"))
                                    }
                                    if (json.has("files.autoSave")) {
                                        val auto = json.getString("files.autoSave")
                                        viewModel.setAutoSave(auto != "off")
                                    }
                                    if (json.has("explorer.showHiddenFiles")) {
                                        viewModel.setShowHidden(json.getBoolean("explorer.showHiddenFiles"))
                                    }

                                    tab = tab.copy(originalContent = tab.content, isDirty = false)
                                    parseError = null
                                    Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    parseError = "JSON parse error: ${e.message}"
                                    Toast.makeText(context, "Invalid JSON: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (tab.isDirty) "Unsaved changes" else "Edit settings.json - changes apply on save",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (tab.isDirty) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                parseError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            SoraEditorView(
                tab = tab,
                onContentChange = { newContent ->
                    tab = tab.copy(content = newContent, isDirty = newContent != tab.originalContent)
                },
                modifier = Modifier.fillMaxSize(),
                fontSize = fontSize,
                wordWrap = wordWrap,
                tabSize = tabSize,
                theme = when (theme) {
                    AppTheme.DARK -> "vscode_dark"
                    AppTheme.LIGHT -> "vscode_dark" // fallback
                    AppTheme.MONOKAI -> "monokai"
                    AppTheme.DRACULA -> "darcula"
                    AppTheme.GITHUB_DARK -> "vscode_dark"
                    AppTheme.SOLARIZED_DARK -> "vscode_dark"
                    AppTheme.SYSTEM -> "vscode_dark"
                }
            )
        }
    }
}
