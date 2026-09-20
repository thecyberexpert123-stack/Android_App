package com.cyberexpert.androde.domain.usecase

import com.cyberexpert.androde.domain.model.User
import com.cyberexpert.androde.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing users.
 * Single responsibility, testable.
 */
class GetUsersUseCase @Inject constructor(
    private val repository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> = repository.getUsers()
}
