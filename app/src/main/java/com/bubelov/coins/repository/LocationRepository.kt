package com.bubelov.coins.repository

import android.content.Context
import android.location.LocationManager
import com.bubelov.coins.model.Location
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Named

class LocationRepository @Inject constructor(
    private val context: Context,
    @Named("default_location")
    private val defaultLocation: Location
) {
    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val location = callbackFlow {
        offer(defaultLocation)
    }
}