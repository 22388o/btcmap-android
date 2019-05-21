package com.bubelov.coins.util

import com.bubelov.coins.model.Location

fun Location.distanceTo(anotherLocation: Location, units: DistanceUnits): Double {
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

fun android.location.Location.toLocation() = Location(latitude, longitude)

//fun Location.toLatLng(): LatLng = LatLng(latitude, longitude)
//
//fun LatLng.toLocation() = Location(latitude, longitude)