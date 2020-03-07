package com.bubelov.coins.repository

sealed class Result<out T: Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val e: Throwable) : Result<Nothing>()
}