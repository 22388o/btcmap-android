package search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.res.Resources
import androidx.lifecycle.viewModelScope
import com.bubelov.coins.R
import etc.Location
import settings.PreferencesRepository
import map.PlacesRepository
import map.PlaceIconsRepository
import etc.DistanceUnits
import etc.DistanceUtils
import db.Place
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.NumberFormat

class PlacesSearchViewModel(
    private val placesRepository: PlacesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val preferencesRepository: PreferencesRepository,
    private val resources: Resources
) : ViewModel() {

    private var searchJob: Job? = null

    private var location: Location? = null

    private val _rows = MutableLiveData<List<PlacesSearchRow>>()
    val rows: LiveData<List<PlacesSearchRow>> = _rows

    fun setUp(location: Location?) {
        this.location = location
    }

    fun setQuery(query: String) {
        searchJob?.cancel()

        if (query.length < MIN_QUERY_LENGTH) {
            _rows.value = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            var places = placesRepository.findBySearchQuery(query)

            val location = location

            if (location != null) {
                places = places.sortedBy {
                    DistanceUtils.getDistance(
                        location.latitude,
                        location.longitude,
                        it.latitude,
                        it.longitude
                    )
                }
            }

            val rows = places.map { it.toRow(location) }

            if (isActive) {
                _rows.value = rows
            }
        }
    }

    private suspend fun Place.toRow(userLocation: Location?): PlacesSearchRow {
        val distanceStringBuilder = StringBuilder()

        if (userLocation != null) {
            val placeLocation = Location(latitude, longitude)
            val distance = userLocation.distanceTo(placeLocation, getDistanceUnits())

            distanceStringBuilder.apply {
                append(DISTANCE_FORMAT.format(distance))
                append(" ")
                append(getDistanceUnits().getShortName())
            }
        }

        return PlacesSearchRow(
            placeId = id,
            name = name,
            distance = distanceStringBuilder.toString(),
            icon = placeIconsRepository.getPlaceIcon(category)
        )
    }

    private suspend fun getDistanceUnits(): DistanceUnits {
        val key = PreferencesRepository.DISTANCE_UNITS_KEY
        val value = preferencesRepository.get(key).first()
        val defaultValue = resources.getString(R.string.pref_distance_units_automatic)

        return if (value.isBlank() || value == defaultValue) {
            DistanceUnits.default
        } else {
            DistanceUnits.valueOf(value)
        }
    }

    private fun DistanceUnits.getShortName(): String {
        return when (this) {
            DistanceUnits.KILOMETERS -> resources.getString(R.string.kilometers_short)
            DistanceUnits.MILES -> resources.getString(R.string.miles_short)
        }
    }

    private fun Location.distanceTo(anotherLocation: Location, units: DistanceUnits): Double {
        val distanceInKilometers = DistanceUtils.getDistance(
            latitude,
            longitude,
            anotherLocation.latitude,
            anotherLocation.longitude
        ) / 1000.0

        return when (units) {
            DistanceUnits.KILOMETERS -> distanceInKilometers
            DistanceUnits.MILES -> DistanceUtils.toMiles(distanceInKilometers)
        }
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 2

        private val DISTANCE_FORMAT = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 1
        }
    }
}