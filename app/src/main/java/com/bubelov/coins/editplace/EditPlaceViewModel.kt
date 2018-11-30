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

package com.bubelov.coins.editplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class EditPlaceViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    coroutineContext: CoroutineContext
) : ViewModel() {

    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    private val _showProgress = MutableLiveData<Boolean>()
    val showProgress: LiveData<Boolean> = _showProgress

    private val _changesSubmitted = LiveEvent<Unit>()
    val changesSubmitted = _changesSubmitted.toSingleEvent()

    private val _error = LiveEvent<String>()
    val error = _error.toSingleEvent()

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun submitChanges(
        originalPlace: Place?,
        updatedPlace: Place
    ) {
        uiScope.launch {
            _showProgress.value = true

            try {
                if (originalPlace == null) {
                    placesRepository.addPlace(updatedPlace)
                } else {
                    placesRepository.updatePlace(updatedPlace)
                }

                _changesSubmitted.call()
            } catch (error: Exception) {
                _error.value = error.message
            } finally {
                _showProgress.value = false
            }
        }
    }
}