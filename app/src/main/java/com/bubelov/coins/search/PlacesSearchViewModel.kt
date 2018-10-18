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

package com.bubelov.coins.search

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.SharedPreferences
import android.content.res.Resources
import com.bubelov.coins.R
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.place.PlacesRepository
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
    private val placeIconsRepository: PlaceIconsRepository,
    private val preferences: SharedPreferences,
    private val resources: Resources,
    coroutineContext: CoroutineContext
) : ViewModel() {
    private val mainJob = Job()
    private var searchJob: Job? = null
    private val uiScope = CoroutineScope(coroutineContext + mainJob)

    private lateinit var currency: String
    private var location: Location? = null

    private val _rows = MutableLiveData<List<PlacesSearchRow>>()
    val rows: LiveData<List<PlacesSearchRow>> = _rows

    fun setUp(currency: String, location: Location?) {
        this.currency = currency
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
                .filter { it.currencies.contains(currency) }

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

    private fun Place.toRow(userLocation: Location?): PlacesSearchRow {
        val distanceStringBuilder = StringBuilder()

        if (userLocation != null) {
            val placeLocation = Location(latitude, longitude)
            val distance = userLocation.distanceTo(placeLocation, getDistanceUnits())
            distanceStringBuilder.append(DISTANCE_FORMAT.format(distance))
            distanceStringBuilder.append(" ")
            distanceStringBuilder.append(getDistanceUnits().getShortName())
        }

        return PlacesSearchRow(
            placeId = id,
            name = name,
            distance = distanceStringBuilder.toString(),
            icon = placeIconsRepository.getPlaceIcon(category)
        )
    }

    private fun getDistanceUnits(): DistanceUnits {
        val distanceUnitsString = preferences.getString(
            resources.getString(R.string.pref_distance_units_key),
            resources.getString(R.string.pref_distance_units_automatic)
        )

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