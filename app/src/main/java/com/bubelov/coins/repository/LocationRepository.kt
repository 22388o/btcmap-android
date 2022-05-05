package com.bubelov.coins.repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.bubelov.coins.model.Location
import com.bubelov.coins.repository.synclogs.LogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("MissingPermission")
class LocationRepository(
    private val context: Context,
    private val locationManager: LocationManager,
    private val defaultLocation: Location,
    private val log: LogsRepository
) {
    private val _location by lazy { MutableStateFlow(getDefaultOrLastKnownLocation()) }
    val location: StateFlow<Location> get() = _location

    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            log += "New location: ${location.latitude}, ${location.longitude}"
            _location.value = Location(
                latitude = location.latitude,
                longitude = location.longitude
            )
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String) {
        }

        override fun onProviderDisabled(provider: String) {
        }
    }

    init {
        log += "LocationRepository.init"
        if (locationPermissionsGranted()) {
            log += "Permissions granted"
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10_000,
                0f,
                listener
            )
        } else {
            log += "No permissions!"
        }
    }

    private fun getDefaultOrLastKnownLocation(): Location {
        if (!locationPermissionsGranted()) {
            return defaultLocation
        } else {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            log += "Last known location: ${lastKnownLocation?.latitude}, ${lastKnownLocation?.longitude}"
            log += "Enabled providers: ${locationManager.getProviders(true)}"

            return if (lastKnownLocation != null) {
                Location(
                    latitude = lastKnownLocation.latitude,
                    longitude = lastKnownLocation.longitude
                )
            } else {
                defaultLocation
            }
        }
    }

    private fun locationPermissionsGranted() = ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}