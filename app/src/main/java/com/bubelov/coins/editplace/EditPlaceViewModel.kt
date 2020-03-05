package com.bubelov.coins.editplace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.toSingleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
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
        Timber.d("Original place: $originalPlace")
        Timber.d("Updated place: $updatedPlace")

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