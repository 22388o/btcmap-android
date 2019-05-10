package com.bubelov.coins.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    context: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(context + job)

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _authorized = MutableLiveData<Boolean>()
    val authorized: LiveData<Boolean> = _authorized

    private val _errorMessage = LiveEvent<String>()
    val errorMessage = _errorMessage.toSingleEvent()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        launchAuthFlow {
            userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun signIn(email: String, password: String) {
        launchAuthFlow {
            userRepository.signIn(email, password)
        }
    }

    private fun launchAuthFlow(block: suspend () -> Unit): Job {
        return uiScope.launch {
            try {
                _showProgress.value = true
                block()
                _authorized.value = true
            } catch (httpException: HttpException) {
                _errorMessage.value =
                    httpException.response()?.errorBody()?.string() ?: httpException.message()
            } catch (t: Throwable) {
                _errorMessage.value = t.message
            } finally {
                _showProgress.value = false
            }
        }
    }
}