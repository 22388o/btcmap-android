package com.bubelov.coins.util

sealed class BasicTaskState {
    object Progress : BasicTaskState()
    object Success : BasicTaskState()
    data class Error(val message: String) : BasicTaskState()
}