package repository

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@SuppressLint("MissingPermission")
class LocationRepository(
    private val context: Context,
    private val locationManager: LocationManager,
    private val defaultLocation: Location,
) {
    private val _location by lazy { MutableStateFlow(getDefaultOrLastKnownLocation()) }
    val location: StateFlow<Location> get() = _location

    private val listener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
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
        if (locationPermissionsGranted()) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10_000,
                0f,
                listener
            )
        }
    }

    private fun getDefaultOrLastKnownLocation(): Location {
        return if (!locationPermissionsGranted()) {
            defaultLocation
        } else {
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

            if (lastKnownLocation != null) {
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