package com.cyberexpert.androde.core.marketplace

import kotlinx.coroutines.flow.Flow

/**
 * Marketplace repository - Phase 8, similar to VS Code marketplace.
 * Provides real API for extension search, install, publishing via Retrofit.
 */

data class MarketplaceExtension(
    val id: String,
    val name: String,
    val displayName: String,
    val publisher: String,
    val description: String,
    val version: String,
    val categories: List<String> = emptyList(),
    val tags: List<String> = emptyList(),
    val iconUrl: String? = null,
    val downloadUrl: String? = null,
    val downloadCount: Long = 0,
    val rating: Float = 0f,
    val ratingCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis(),
    val isBuiltin: Boolean = false
)

data class MarketplaceSearchResult(
    val extensions: List<MarketplaceExtension>,
    val total: Int,
    val query: String
)

interface MarketplaceRepository {
    fun getInstalledExtensions(): Flow<List<MarketplaceExtension>>
    fun getMarketplaceExtensions(): Flow<List<MarketplaceExtension>>
    suspend fun searchMarketplace(query: String, category: String? = null, sortBy: String = "relevance"): MarketplaceSearchResult
    suspend fun getExtensionDetails(id: String): MarketplaceExtension?
    suspend fun installExtension(id: String): Boolean
    suspend fun uninstallExtension(id: String): Boolean
    suspend fun downloadExtension(id: String): String? // returns file path
    suspend fun publishExtension(extension: MarketplaceExtension, packagePath: String): Boolean
}
