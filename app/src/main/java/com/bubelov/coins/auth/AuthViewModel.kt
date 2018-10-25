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

package com.bubelov.coins.auth

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.ConsumableValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    coroutineContext: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _authorized = MutableLiveData<Boolean>()
    val authorized: LiveData<Boolean> = _authorized

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<ConsumableValue<String>> =
        Transformations.map(_errorMessage) { ConsumableValue(it) }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun signIn(googleToken: String) {
        launchAuthFlow {
            userRepository.signIn(googleToken)
        }
    }

    fun signIn(email: String, password: String) {
        launchAuthFlow {
            userRepository.signIn(email, password)
        }
    }

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        launchAuthFlow {
            userRepository.signUp(email, password, firstName, lastName)
        }
    }

    private fun launchAuthFlow(block: suspend () -> Unit): Job {
        return uiScope.launch {
            try {
                _showProgress.value = true
                block()
            } catch (error: Exception) {
                _errorMessage.value = error.message
            } finally {
                _showProgress.value = false
            }
        }
    }
}