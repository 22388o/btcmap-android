package map

import androidx.lifecycle.ViewModel
import etc.LocationRepository
import db.Place
import kotlinx.coroutines.flow.*

class MapViewModel(
    private val placesRepository: PlacesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
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

    suspend fun selectPlace(id: String) {
        selectedPlace = placesRepository.find(id)
        selectedPlaceFlow.update { selectedPlace }
    }
}