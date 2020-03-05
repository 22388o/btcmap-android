package com.bubelov.coins.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.content.SharedPreferences
import android.content.res.Resources
import com.bubelov.coins.R
import com.bubelov.coins.data.Place
import com.bubelov.coins.model.Location
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import com.bubelov.coins.util.DistanceUnits
import com.bubelov.coins.util.DistanceUtils
import com.bubelov.coins.util.distanceTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.NumberFormat
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PlacesSearchViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val preferences: SharedPreferences,
    private val resources: Resources,
    coroutineContext: CoroutineContext
) : ViewModel() {
    private val mainJob = Job()
    private var searchJob: Job? = null
    private val uiScope = CoroutineScope(coroutineContext + mainJob)

    private var location: Location? = null

    private val _rows = MutableLiveData<List<PlacesSearchRow>>()
    val rows: LiveData<List<PlacesSearchRow>> = _rows

    fun setUp(location: Location?) {
        this.location = location
    }

    override fun onCleared() {
        super.onCleared()
        mainJob.cancel()
    }

    fun setQuery(query: String) {
        searchJob?.cancel()

        if (query.length < MIN_QUERY_LENGTH) {
            _rows.value = emptyList()
            return
        }

        searchJob = uiScope.launch {
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
            icon = placeIconsRepository.getPlaceIcon(
                placeCategoriesRepository.findById(categoryId)?.name ?: ""
            )
        )
    }

    private fun getDistanceUnits(): DistanceUnits {
        val distanceUnitsString = preferences.getString(
            resources.getString(R.string.pref_distance_units_key),
            resources.getString(R.string.pref_distance_units_automatic)
        )!!

        return if (distanceUnitsString == resources.getString(R.string.pref_distance_units_automatic)) {
            DistanceUnits.default
        } else {
            DistanceUnits.valueOf(distanceUnitsString)
        }
    }

    private fun DistanceUnits.getShortName(): String {
        return when (this) {
            DistanceUnits.KILOMETERS -> resources.getString(R.string.kilometers_short)
            DistanceUnits.MILES -> resources.getString(R.string.miles_short)
        }
    }

    companion object {
        private const val MIN_QUERY_LENGTH = 2

        private val DISTANCE_FORMAT = NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = 1
        }
    }
}