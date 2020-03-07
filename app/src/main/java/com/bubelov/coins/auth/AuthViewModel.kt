package com.bubelov.coins.auth

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<AuthState> {
        return getAuthFlow {
            userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun signIn(email: String, password: String): Flow<AuthState> {
        return getAuthFlow {
            userRepository.signIn(email, password)
        }
    }

    private fun getAuthFlow(authFunction: suspend () -> Unit): Flow<AuthState> {
        return flow {
            emit(AuthState.Progress)

            try {
                authFunction()
                emit(AuthState.Success)
            } catch (httpException: HttpException) {
                val message = withContext(Dispatchers.IO) {
                    httpException.response()?.errorBody()?.string() ?: "Unknown error"
                }

                emit(AuthState.Error(message))
            } catch (t: Throwable) {
                emit(AuthState.Error(t.message ?: ""))
            }
        }
    }
}