package com.bubelov.coins.model

import org.joda.time.DateTime

data class User(
    val id: String,
    val email: String,
    val emailConfirmed: Boolean,
    val firstName: String,
    val lastName: String,
    val avatarUrl: String,
    val createdAt: DateTime,
    val updatedAt: DateTime
)