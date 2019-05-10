package com.bubelov.coins.api.coins

import com.bubelov.coins.model.User

data class TokenResponse(
    val token: String,
    val user: User
)