package com.cyberexpert.androde.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * DTO from remote API (jsonplaceholder.typicode.com/users).
 * Validation happens during mapping to domain.
 */
@Serializable
data class UserDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
)

@Serializable
data class CompanyDto(
    val name: String,
    val catchPhrase: String,
    val bs: String
)

@Serializable
data class AddressDto(
    val street: String,
    val suite: String,
    val city: String,
    val zipcode: String
)
