package com.cyberexpert.androde.domain.usecase

import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Use case for refreshing users from remote.
 */
class RefreshUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): AppResult<Unit> = repository.refreshUsers()
}
