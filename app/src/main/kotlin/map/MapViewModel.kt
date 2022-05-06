package map

import androidx.lifecycle.ViewModel
import repository.LocationRepository
import repository.place.PlacesRepository
import repository.placeicon.PlaceIconsRepository
import repository.user.UserRepository
import db.Place
import kotlinx.coroutines.flow.*

class MapViewModel(
    private val placesRepository: PlacesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    val userRepository: UserRepository,
    locationRepository: LocationRepository,
) : ViewModel() {

    val selectedPlaceFlow: MutableStateFlow<Place?> = MutableStateFlow(null)

    private var selectedPlace: Place? = null

    val location = locationRepository.location

    val allPlaces = placesRepository.getAll()

    fun getMarkers(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ) = placesRepository.get(
        minLat = minLat,
        maxLat = maxLat,
        minLon = minLon,
        maxLon = maxLon
    ).map { places ->
        places.map {
            PlaceMarker(
                placeId = it.id,
                icon = placeIconsRepository.getMarker(it.category),
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    val placeMarkers = allPlaces.map { places ->
        places.map {
            PlaceMarker(
                placeId = it.id,
                icon = placeIconsRepository.getMarker(it.category),
                latitude = it.latitude,
                longitude = it.longitude
            )
        }
    }

    private var initializedLocation = false

    var postAuthAction = PostAuthAction.DO_NOTHING

    suspend fun selectPlace(id: String) {
        selectedPlace = placesRepository.find(id)
        selectedPlaceFlow.update { selectedPlace }
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