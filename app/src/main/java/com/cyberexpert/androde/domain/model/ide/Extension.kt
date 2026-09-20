package com.cyberexpert.androde.domain.model.ide

/**
 * Extension model, similar to VS Code Extensions view.
 */
data class Extension(
    val id: String,
    val name: String,
    val displayName: String,
    val description: String,
    val version: String,
    val publisher: String,
    val categories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val iconUrl: String? = null,
    val isInstalled: Boolean = false,
    val isEnabled: Boolean = true,
    val isBuiltin: Boolean = false,
    val installPath: String? = null,
    val contributesLanguages: List<String> = emptyList(),
    val contributesThemes: List<String> = emptyList()
)

data class ExtensionState(
    val installed: List<Extension> = emptyList(),
    val marketplace: List<Extension> = emptyList(),
    val isLoadingMarketplace: Boolean = false,
    val searchQuery: String = ""
)
