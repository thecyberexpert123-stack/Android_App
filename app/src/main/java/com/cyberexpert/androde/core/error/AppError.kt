package com.cyberexpert.androde.core.error

/**
 * Sealed hierarchy for explicit error handling.
 * Each error provides user-facing message and optional technical details.
 */
sealed class AppError {
    abstract val userMessage: String
    abstract val cause: Throwable?

    data class Network(
        override val userMessage: String = "No internet connection. Please check your network.",
        override val cause: Throwable? = null
    ) : AppError()

    data class Server(
        val code: Int,
        override val userMessage: String = "Server error ($code). Please try again later.",
        override val cause: Throwable? = null
    ) : AppError()

    data class Local(
        override val userMessage: String = "Local storage error.",
        override val cause: Throwable? = null
    ) : AppError()

    data class Validation(
        override val userMessage: String,
        override val cause: Throwable? = null
    ) : AppError()

    data class Unauthorized(
        override val userMessage: String = "Session expired. Please log in again.",
        override val cause: Throwable? = null
    ) : AppError()

    data class Unknown(
        override val userMessage: String = "Something went wrong. Please try again.",
        override val cause: Throwable? = null
    ) : AppError()
}
