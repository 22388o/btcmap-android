package com.bubelov.coins.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class MapViewModel(
    private val placesRepository: PlacesRepository,
    val userRepository: UserRepository,
    locationRepository: LocationRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository
) : ViewModel() {

    val selectedPlaceFlow = flow {
        while (true) {
            emit(selectedPlace)
            delay(100)
        }
    }

    private var selectedPlace: Place? = null

    val locationFlow = locationRepository.location

    val allPlaces = placesRepository.getAll()

    val placeMarkers = allPlaces.map { places ->
        places.map {
            PlaceMarker(
                placeId = it.id,
                icon = placeIconsRepository.getMarker(
                    placeCategoriesRepository.findById(it.categoryId)?.name ?: ""
                ),
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    private var initializedLocation = false

    var postAuthAction = PostAuthAction.DO_NOTHING

    suspend fun selectPlace(id: String) {
        selectedPlace = placesRepository.find(id)
    }

    suspend fun onAddPlaceClick(): AddPlaceClickResult {
        return if (userRepository.getToken().isNotBlank()) {
            AddPlaceClickResult.ALLOWED
        } else {
            postAuthAction = PostAuthAction.ADD_PLACE
            AddPlaceClickResult.UNAUTHORIZED
        }
    }

    suspend fun onEditPlaceClick(): EditPlaceClickResult {
        return if (userRepository.getToken().isNotBlank()) {
            EditPlaceClickResult.ALLOWED
        } else {
            postAuthAction = PostAuthAction.EDIT_SELECTED_PLACE
            EditPlaceClickResult.UNAUTHORIZED
        }
    }

    suspend fun onDrawerHeaderClick(): DrawerHeaderClickResult {
        return if (userRepository.getToken().isNotBlank()) {
            DrawerHeaderClickResult.SHOW_USER_PROFILE
        } else {
            postAuthAction = PostAuthAction.DO_NOTHING
            DrawerHeaderClickResult.REQUIRE_AUTH
        }
    }

    enum class AddPlaceClickResult {
        ALLOWED,
        UNAUTHORIZED
    }

    enum class EditPlaceClickResult {
        ALLOWED,
        UNAUTHORIZED
    }

    enum class DrawerHeaderClickResult {
        SHOW_USER_PROFILE,
        REQUIRE_AUTH
    }
}