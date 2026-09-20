package com.cyberexpert.androde.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for local cache.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val updatedAt: Long = System.currentTimeMillis()
)
