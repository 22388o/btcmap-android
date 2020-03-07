package com.bubelov.coins.repository.user

import android.content.SharedPreferences
import android.util.Base64
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreateUserArgs
import com.bubelov.coins.model.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(
    private val api: CoinsApi,
    private val preferences: SharedPreferences,
    private val gson: Gson
) {

    fun getUser(): User? {
        return gson.fromJson(preferences.getString(USER_KEY, null), User::class.java)
    }

    private fun setUser(user: User) {
        preferences.edit().putString(USER_KEY, gson.toJson(user)).apply()
    }

    fun getToken(): String {
        return preferences.getString(TOKEN_KEY, null) ?: ""
    }

    private fun setToken(token: String) {
        preferences.edit().putString(TOKEN_KEY, token).apply()
    }

    suspend fun signIn(email: String, password: String) {
        withContext(Dispatchers.IO) {
            val tokenResponse = api.getToken(getBasicAuthHeader(email, password))
            setUser(tokenResponse.user)
            setToken(tokenResponse.token)
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ) {
        val createUserArgs = CreateUserArgs(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName
        )

        withContext(Dispatchers.IO) {
            api.createUser(createUserArgs)
            val tokenResponse = api.getToken(getBasicAuthHeader(email, password))
            setUser(tokenResponse.user)
            setToken(tokenResponse.token)
        }
    }

    fun clear() {
        preferences.edit().remove(USER_KEY).remove(TOKEN_KEY).apply()
    }

    private fun getBasicAuthHeader(email: String, password: String): String {
        val credentialsInBase64 = Base64.encodeToString(
            "$email:$password".toByteArray(),
            Base64.NO_WRAP
        )

        return "Basic $credentialsInBase64"
    }

    companion object {
        private const val USER_KEY = "user"
        private const val TOKEN_KEY = "token"
    }
}