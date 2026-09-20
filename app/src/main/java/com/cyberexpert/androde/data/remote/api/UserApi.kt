package com.cyberexpert.androde.data.remote.api

import com.cyberexpert.androde.data.remote.dto.UserDto
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API definition.
 * Uses jsonplaceholder as sample backend - replace with real backend via BuildConfig.API_BASE_URL.
 */
interface UserApi {
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto
}
