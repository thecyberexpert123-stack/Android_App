package com.cyberexpert.androde.domain.usecase

import app.cash.turbine.test
import com.cyberexpert.androde.domain.model.User
import com.cyberexpert.androde.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUsersUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUsersUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetUsersUseCase(repository)
    }

    @Test
    fun `invoke returns flow of users from repository`() = runTest {
        val users = listOf(
            User(1, "John Doe", "johndoe", "john@example.com", "123", "example.com"),
            User(2, "Jane Doe", "janedoe", "jane@example.com", "456", "example.org")
        )
        every { repository.getUsers() } returns flowOf(users)

        useCase().test {
            val result = awaitItem()
            assertEquals(users, result)
            awaitComplete()
        }
    }

    @Test
    fun `invoke returns empty list when repository empty`() = runTest {
        every { repository.getUsers() } returns flowOf(emptyList())

        useCase().test {
            val result = awaitItem()
            assertEquals(0, result.size)
            awaitComplete()
        }
    }
}
