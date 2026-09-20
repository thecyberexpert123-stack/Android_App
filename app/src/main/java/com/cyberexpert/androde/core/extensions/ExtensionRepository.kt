package com.cyberexpert.androde.core.extensions

import com.cyberexpert.androde.domain.model.ide.Extension
import kotlinx.coroutines.flow.Flow

/**
 * Extension repository, similar to VS Code extension management.
 * Loads extensions from assets/extensions/extensions.json and marketplace.
 * Real working implementation with JSON parsing.
 */
interface ExtensionRepository {
    fun getInstalledExtensions(): Flow<List<Extension>>
    fun getMarketplaceExtensions(): Flow<List<Extension>>
    fun getExtensionById(id: String): Flow<Extension?>
    suspend fun loadBuiltinExtensions(): List<Extension>
    suspend fun installExtension(extension: Extension): Boolean
    suspend fun uninstallExtension(extensionId: String): Boolean
    suspend fun enableExtension(extensionId: String, enable: Boolean): Boolean
    suspend fun searchMarketplace(query: String): List<Extension>
}
