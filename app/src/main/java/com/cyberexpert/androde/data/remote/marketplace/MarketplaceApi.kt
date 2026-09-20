package com.cyberexpert.androde.data.remote.marketplace

import com.cyberexpert.androde.core.marketplace.MarketplaceExtension
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Marketplace API - Phase 8, Retrofit interface for VS Code-like marketplace.
 * Real working with Retrofit, would connect to Open VSX or custom backend.
 * For MVP, uses local assets fallback with real network attempt.
 */
interface MarketplaceApi {

    @GET("extensions")
    suspend fun searchExtensions(
        @Query("query") query: String,
        @Query("category") category: String? = null,
        @Query("sortBy") sortBy: String = "relevance",
        @Query("size") size: Int = 20
    ): MarketplaceApiResponse

    @GET("extensions/{id}")
    suspend fun getExtension(
        @Path("id") id: String
    ): MarketplaceExtension

    @GET("extensions/{id}/download")
    suspend fun getDownloadUrl(
        @Path("id") id: String
    ): DownloadUrlResponse
}

data class MarketplaceApiResponse(
    val extensions: List<MarketplaceExtension>,
    val total: Int,
    val offset: Int = 0
)

data class DownloadUrlResponse(
    val downloadUrl: String,
    val version: String
)
