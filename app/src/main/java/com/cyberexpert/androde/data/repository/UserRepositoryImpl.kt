package com.cyberexpert.androde.data.repository

import com.cyberexpert.androde.core.error.AppError
import com.cyberexpert.androde.core.result.AppResult
import com.cyberexpert.androde.data.local.dao.UserDao
import com.cyberexpert.androde.data.local.entity.UserEntity
import com.cyberexpert.androde.data.remote.api.UserApi
import com.cyberexpert.androde.data.remote.dto.UserDto
import com.cyberexpert.androde.domain.model.User
import com.cyberexpert.androde.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementation: single source of truth is Room.
 * Remote is fetched and cached.
 * All exceptions mapped to AppError, never thrown.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val api: UserApi,
    private val dao: UserDao
) : UserRepository {

    override fun getUsers(): Flow<List<User>> {
        return dao.observeUsers().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUserById(id: Int): Flow<User?> {
        return dao.observeUserById(id).map { it?.toDomain() }
    }

    override suspend fun refreshUsers(): AppResult<Unit> {
        return try {
            val dtos = api.getUsers()
            val entities = dtos.map { it.toEntity() }
            dao.insertUsers(entities)
            AppResult.Success(Unit)
        } catch (e: IOException) {
            AppResult.Error(AppError.Network(cause = e))
        } catch (e: HttpException) {
            val error = when (e.code()) {
                401 -> AppError.Unauthorized(cause = e)
                in 500..599 -> AppError.Server(e.code(), cause = e)
                else -> AppError.Server(e.code(), cause = e)
            }
            AppResult.Error(error)
        } catch (e: Exception) {
            AppResult.Error(AppError.Unknown(cause = e))
        }
    }

    // Mappers - kept inside repository to avoid extra files for single slice
    private fun UserDto.toEntity(): UserEntity {
        // Basic validation
        require(id > 0) { "Invalid id" }
        require(name.isNotBlank()) { "Invalid name" }
        return UserEntity(
            id = id,
            name = name.trim(),
            username = username.trim(),
            email = email.trim(),
            phone = phone.trim(),
            website = website.trim()
        )
    }

    private fun UserEntity.toDomain(): User {
        return User(
            id = id,
            name = name,
            username = username,
            email = email,
            phone = phone,
            website = website
        )
    }
}
