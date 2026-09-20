package com.cyberexpert.androde.domain.repository

import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface in domain layer (dependency rule).
 */
interface UserRepository {
    /**
     * Observes users from local cache (Room) as Flow.
     * Single source of truth for UI.
     */
    fun getUsers(): Flow<List<User>>

    /**
     * Refreshes users from remote and saves to local.
     * Returns Result for explicit error handling.
     */
    suspend fun refreshUsers(): AppResult<Unit>

    /**
     * Get user by id from local.
     */
    fun getUserById(id: Int): Flow<User?>
}
