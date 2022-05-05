package repository.user

import android.util.Base64
import api.coins.CoinsApi
import api.coins.CreateUserArgs
import model.User
import repository.PreferencesRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class UserRepository(
    private val api: CoinsApi,
    private val preferencesRepository: PreferencesRepository,
    private val gson: Gson
) {

    suspend fun getUser(): User? {
        return gson.fromJson(preferencesRepository.get(USER_KEY).first(), User::class.java)
    }

    private suspend fun setUser(user: User) {
        preferencesRepository.put(
            key = USER_KEY,
            value = gson.toJson(user)
        )
    }

    suspend fun getToken(): String {
        return preferencesRepository.get(TOKEN_KEY).first()
    }

    private suspend fun setToken(token: String) {
        preferencesRepository.put(
            key = TOKEN_KEY,
            value = token
        )
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

    suspend fun clear() {
        preferencesRepository.put(USER_KEY, "") // TODO remove
        preferencesRepository.put(TOKEN_KEY, "") // TODO remove
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