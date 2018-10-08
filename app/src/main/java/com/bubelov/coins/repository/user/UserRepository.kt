/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.repository.user

import android.arch.lifecycle.MutableLiveData
import android.content.SharedPreferences
import com.bubelov.coins.api.await

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreateUserArgs
import com.bubelov.coins.model.User
import com.bubelov.coins.util.AsyncResult
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: CoinsApi,
    private val preferences: SharedPreferences,
    private val gson: Gson
) {
    var user: User?
        get() = gson.fromJson(preferences.getString(USER_KEY, null), User::class.java)
        set(user) = preferences.edit().putString(USER_KEY, gson.toJson(user)).apply()

    var userAuthToken: String
        get() = preferences.getString(API_AUTH_TOKEN_KEY, "")
        set(token) = preferences.edit().putString(API_AUTH_TOKEN_KEY, token).apply()

    var userAuthMethod: String
        get() = preferences.getString(API_AUTH_METHOD_KEY, "")
        set(method) = preferences.edit().putString(API_AUTH_METHOD_KEY, method).apply()

    suspend fun signIn(googleToken: String) {
        withContext(Dispatchers.IO) {
            val response = api.authWithGoogle(googleToken).await()
            user = response.user
            userAuthToken = response.token
            userAuthMethod = "google"
        }
    }

    suspend fun signIn(email: String, password: String) {
        withContext(Dispatchers.IO) {
            val response = api.authWithEmail(email, password).await()
            user = response.user
            userAuthToken = response.token
            userAuthMethod = "email"
        }

        val result = MutableLiveData<AsyncResult<Any>>()
        result.postValue(AsyncResult.Loading)
    }

    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        val args = CreateUserArgs(
            CreateUserArgs.User(
                email = email,
                password = password,
                firstName = firstName,
                lastName = lastName
            )
        )

        withContext(Dispatchers.IO) {
            val response = api.createUser(args).await()
            user = response.user
            userAuthToken = response.token
            userAuthMethod = "email"
        }
    }

    fun signedIn() = !userAuthToken.isBlank()

    fun clear() {
        user = null
        userAuthToken = ""
        userAuthMethod = ""
    }

    companion object {
        private const val USER_KEY = "user"
        private const val API_AUTH_TOKEN_KEY = "api_auth_token"
        private const val API_AUTH_METHOD_KEY = "api_auth_method"
    }
}