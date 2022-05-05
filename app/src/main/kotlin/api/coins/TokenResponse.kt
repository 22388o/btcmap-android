package api.coins

import model.User

data class TokenResponse(
    val token: String,
    val user: User
)