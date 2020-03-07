package com.bubelov.coins.auth

sealed class AuthState {
    object Progress : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}