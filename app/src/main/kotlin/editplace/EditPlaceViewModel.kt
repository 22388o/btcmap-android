package editplace

import androidx.lifecycle.ViewModel
import repository.place.PlacesRepository
import etc.BasicTaskState
import db.Place
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class EditPlaceViewModel(
    private val placesRepository: PlacesRepository
) : ViewModel() {

    fun submitChanges(
        originalPlace: Place?,
        updatedPlace: Place
    ): Flow<BasicTaskState> {
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