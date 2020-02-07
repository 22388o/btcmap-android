package com.bubelov.coins.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.LocationRepository
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.LiveEvent
import com.bubelov.coins.util.LocationLiveData
import com.bubelov.coins.util.toSingleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MapViewModel @Inject constructor(
    private val notificationAreaRepository: NotificationAreaRepository,
    private val placesRepository: PlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val location: LocationLiveData,
    val userRepository: UserRepository,
    val currenciesRepository: CurrenciesRepository,
    val currenciesPlacesRepository: CurrenciesPlacesRepository,
    val locationRepository: LocationRepository,
    coroutineContext: CoroutineContext
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place> = _selectedPlace

    var navigateToNextSelectedPlace = false

    var callback: Callback? = null

    val locationFlow = locationRepository.location

    val allPlaces = placesRepository.getAll()

//    var mapBounds = MutableLiveData<LatLngBounds>()

//    private val places: LiveData<List<Place>> =
//        Transformations.switchMap(mapBounds) { placesRepository.getPlaces(it) }
//
//    val placeMarkers: LiveData<List<PlaceMarker>> = Transformations.switchMap(places) { places ->
//        MutableLiveData<List<PlaceMarker>>().apply {
//            uiScope.launch {
//                value = places.map {
//                    PlaceMarker(
//                        placeId = it.id,
//                        icon = placeIconsRepository.getMarker(
//                            placeCategoriesRepository.findById(it.categoryId)?.name ?: ""
//                        ),
//                        latLng = LatLng(it.latitude, it.longitude)
//                    )
//                }
//            }
//        }
//    }

    val userLocation: LiveData<Location> = Transformations.map(location) { location ->
        if (location != null && notificationAreaRepository.notificationArea == null) {
            notificationAreaRepository.notificationArea = NotificationArea(
                location.latitude,
                location.longitude,
                NotificationAreaRepository.DEFAULT_RADIUS_METERS
            )
        }

        location
    }

    private val _openSignInScreen = LiveEvent<Void>()
    val openSignInScreen = _openSignInScreen.toSingleEvent()

    private val _openAddPlaceScreen = LiveEvent<Void>()
    val openAddPlaceScreen = _openAddPlaceScreen.toSingleEvent()

    private val _openEditPlaceScreen = LiveEvent<Void>()
    val openEditPlaceScreen = _openEditPlaceScreen.toSingleEvent()

    private val _moveMapToLocation = LiveEvent<Location>()
    val moveMapToLocation = _moveMapToLocation.toSingleEvent()

    private val _requestLocationPermissions = LiveEvent<Void>()
    val requestLocationPermissions = _requestLocationPermissions.toSingleEvent()

    private var initializedLocation = false

    private var postAuthAction = PostAuthAction.DO_NOTHING

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun selectPlace(id: String) {
        Timber.d("Selecting place with id = $id")

        uiScope.launch {
            val place = placesRepository.find(id)
            Timber.d("Loaded place by id, result: $place")
            Timber.d("Previously selected place: ${_selectedPlace.value}")
            _selectedPlace.value = place
        }
    }

    fun moveToLocation(location: Location) {
        initializedLocation = true
        _moveMapToLocation.value = location
    }

    fun onAddPlaceClick() {
        if (userRepository.getToken().isNotBlank()) {
            _openAddPlaceScreen.call()
        } else {
            postAuthAction = PostAuthAction.ADD_PLACE
            _openSignInScreen.call()
        }
    }

    fun onEditPlaceClick() {
        if (userRepository.getToken().isNotBlank()) {
            _openEditPlaceScreen.call()
        } else {
            postAuthAction = PostAuthAction.EDIT_SELECTED_PLACE
            _openSignInScreen.call()
        }
    }

    fun onDrawerHeaderClick() {
        if (userRepository.getToken().isNotBlank()) {
            callback?.showUserProfile()
        } else {
            postAuthAction = PostAuthAction.DO_NOTHING
            _openSignInScreen.call()
        }
    }

    fun onLocationButtonClick() {
        if (!location.isLocationPermissionGranted()) {
            _requestLocationPermissions.call()
            return
        }

        location.value?.let { _moveMapToLocation.value = it }
    }

    fun onReturnFromLocationSettings() {
        if (!location.isLocationPermissionGranted()) {
            _requestLocationPermissions.call()
        }
    }

    fun onLocationPermissionGranted() {
        location.onLocationPermissionGranted()

        if (!initializedLocation) {
            location.value?.let { _moveMapToLocation.value = it }
            initializedLocation = true
        }
    }

    fun onMapReady() {
        if (!location.isLocationPermissionGranted()) {
            _requestLocationPermissions.call()
            return
        }

        if (initializedLocation) {
            return
        }

        if (selectedPlace.value == null) {
            location.value?.let { _moveMapToLocation.value = it }
            initializedLocation = true
        }
    }

    fun onAuthSuccess() {
        when (postAuthAction) {
            PostAuthAction.DO_NOTHING -> {
                // Do nothing :)
            }

            PostAuthAction.ADD_PLACE -> {
                _openAddPlaceScreen.call()
            }

            PostAuthAction.EDIT_SELECTED_PLACE -> {
                _openEditPlaceScreen.call()
            }
        }
    }

    interface Callback {
        fun showUserProfile()
    }
}