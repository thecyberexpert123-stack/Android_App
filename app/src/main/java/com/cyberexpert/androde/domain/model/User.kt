package com.cyberexpert.androde.domain.model

/**
 * Domain model - pure Kotlin, no Android dependencies.
 * Represents a user in the business logic.
 */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
) {
    init {
        require(id > 0) { "User id must be positive" }
        require(name.isNotBlank()) { "Name must not be blank" }
        require(email.contains("@")) { "Invalid email format" }
    }
}
