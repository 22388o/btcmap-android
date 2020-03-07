package com.bubelov.coins.repository

import android.content.Context
import android.location.LocationManager
import com.bubelov.coins.model.Location
import kotlinx.coroutines.flow.callbackFlow

class LocationRepository(
    private val context: Context,
    private val defaultLocation: Location
) {
    private val locationManager by lazy {
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    val location = callbackFlow {
        offer(defaultLocation)
    }
}