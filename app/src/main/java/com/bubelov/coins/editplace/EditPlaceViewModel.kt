package com.bubelov.coins.editplace

import androidx.lifecycle.ViewModel
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.util.BasicTaskState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber

class EditPlaceViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    fun submitChanges(
        originalPlace: Place?,
        updatedPlace: Place
    ): Flow<BasicTaskState> {
        Timber.d("Original place: $originalPlace")
        Timber.d("Updated place: $updatedPlace")

        return flow {
            emit(BasicTaskState.Progress)

            try {
                if (originalPlace == null) {
                    placesRepository.addPlace(updatedPlace)
                } else {
                    placesRepository.updatePlace(updatedPlace)
                }

                emit(BasicTaskState.Success)
            } catch (httpException: HttpException) {
                val message = withContext(Dispatchers.IO) {
                    httpException.response()?.errorBody()?.string() ?: "Unknown error"
                }

                emit(BasicTaskState.Error(message))
            } catch (e: Exception) {
                emit(BasicTaskState.Error(e.message ?: ""))
            }
        }
    }
}