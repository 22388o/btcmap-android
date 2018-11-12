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

package com.bubelov.coins.map

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.ConsumableValue
import com.bubelov.coins.util.LocationLiveData
import com.bubelov.coins.util.SelectedCurrencyLiveData
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MapViewModel @Inject constructor(
    private val notificationAreaRepository: NotificationAreaRepository,
    private val placesRepository: PlacesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val location: LocationLiveData,
    val userRepository: UserRepository,
    val selectedCurrency: SelectedCurrencyLiveData,
    coroutineContext: CoroutineContext
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(coroutineContext + job)

    private val _selectedPlace = MutableLiveData<Place>()
    val selectedPlace: LiveData<Place> = _selectedPlace

    var navigateToNextSelectedPlace = false

    var callback: Callback? = null

    var mapBounds = MutableLiveData<LatLngBounds>()

    private val places: LiveData<List<Place>> =
        Transformations.switchMap(mapBounds) { placesRepository.getPlaces(it) }

    val placeMarkers: LiveData<List<PlaceMarker>> = Transformations.switchMap(places) { places ->
        Transformations.switchMap(selectedCurrency) { currency ->
            MutableLiveData<List<PlaceMarker>>().apply {
                value = places.filter { it.currencies.contains(currency) }.mapTo(ArrayList()) {
                    PlaceMarker(
                        placeId = it.id,
                        icon = placeIconsRepository.getMarker(it.category),
                        latitude = it.latitude,
                        longitude = it.longitude
                    )
                }
            }
        }
    }

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

    private val _shouldOpenSignInScreen = MutableLiveData<Boolean>()
    val shouldOpenSignInScreen: LiveData<ConsumableValue<Boolean>> =
        Transformations.map(_shouldOpenSignInScreen) { ConsumableValue(it) }

    private val _shouldOpenAddPlaceScreen = MutableLiveData<Boolean>()
    val shouldOpenAddPlaceScreen: LiveData<ConsumableValue<Boolean>> =
        Transformations.map(_shouldOpenAddPlaceScreen) { ConsumableValue(it) }

    private val _shouldOpenEditPlaceScreen = MutableLiveData<Boolean>()
    val shouldOpenEditPlaceScreen: LiveData<ConsumableValue<Boolean>> =
        Transformations.map(_shouldOpenEditPlaceScreen) { ConsumableValue(it) }

    private val _shouldMoveMapToLocation = MutableLiveData<Location>()
    val shouldMoveMapToLocation: LiveData<ConsumableValue<Location>> =
        Transformations.map(_shouldMoveMapToLocation) { ConsumableValue(it) }

    private val _shouldRequestLocationPermissions = MutableLiveData<Boolean>()
    val shouldRequestLocationPermissions: LiveData<ConsumableValue<Boolean>> =
        Transformations.map(_shouldRequestLocationPermissions) { ConsumableValue(it) }

    var initializedLocation = false

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun selectPlace(id: Long) {
        uiScope.launch {
            val place = placesRepository.find(id)
            _selectedPlace.value = place
        }
    }

    fun onAddPlaceClick() {
        if (userRepository.signedIn()) {
            _shouldOpenAddPlaceScreen.value = true
        } else {
            _shouldOpenSignInScreen.value = true
        }
    }

    fun onEditPlaceClick() {
        if (userRepository.signedIn()) {
            _shouldOpenEditPlaceScreen.value = true
        } else {
            _shouldOpenSignInScreen.value = true
        }
    }

    fun onDrawerHeaderClick() {
        if (userRepository.signedIn()) {
            callback?.showUserProfile()
        } else {
            _shouldOpenSignInScreen.value = true
        }
    }

    fun onLocationButtonClick() {
        if (!location.isLocationPermissionGranted()) {
            _shouldRequestLocationPermissions.value = true
            return
        }

        location.value?.let { _shouldMoveMapToLocation.value = it }
    }

    fun onReturnFromLocationSettings() {
        if (!location.isLocationPermissionGranted()) {
            _shouldRequestLocationPermissions.value = true
        }
    }

    fun onLocationPermissionGranted() {
        location.onLocationPermissionGranted()
        location.value?.let { _shouldMoveMapToLocation.value = it }
    }

    fun onMapReady() {
        if (!location.isLocationPermissionGranted()) {
            _shouldRequestLocationPermissions.value = true
            return
        }

        if (initializedLocation) {
            return
        }

        if (selectedPlace.value == null) {
            location.value?.let { _shouldMoveMapToLocation.value = it }
            initializedLocation = true
        }
    }

    interface Callback {
        fun showUserProfile()
    }
}