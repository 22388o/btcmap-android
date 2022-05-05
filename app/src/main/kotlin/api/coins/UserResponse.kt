package api.coins

import java.time.LocalDateTime

data class UserResponse(
    val id: String,
    val email: String,
    val emailConfirmed: Boolean,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)