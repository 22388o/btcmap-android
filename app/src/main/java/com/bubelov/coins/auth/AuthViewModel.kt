package com.bubelov.coins.auth

import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.BasicTaskState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@ExperimentalCoroutinesApi
class AuthViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    fun signUp(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Flow<BasicTaskState> {
        return getAuthFlow {
            userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun signIn(email: String, password: String): Flow<BasicTaskState> {
        return getAuthFlow {
            userRepository.signIn(email, password)
        }
    }

    private fun getAuthFlow(authFunction: suspend () -> Unit): Flow<BasicTaskState> {
        return flow {
            emit(BasicTaskState.Progress)

            try {
                authFunction()
                emit(BasicTaskState.Success)
            } catch (httpException: HttpException) {
                val message = withContext(Dispatchers.IO) {
                    httpException.response()?.errorBody()?.string() ?: "Unknown error"
                }

                emit(BasicTaskState.Error(message))
            } catch (t: Throwable) {
                emit(BasicTaskState.Error(t.message ?: ""))
            }
        }
    }
}