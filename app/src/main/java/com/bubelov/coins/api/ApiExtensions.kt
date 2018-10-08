package com.bubelov.coins.api

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun <T> Call<T>.await(): T {
    return kotlin.coroutines.suspendCoroutine { continuation ->
        enqueue(object: Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                continuation.resume(response.body()!!)
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}