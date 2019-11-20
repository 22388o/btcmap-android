package com.bubelov.coins.util

import android.Manifest
import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.bubelov.coins.model.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("MissingPermission")
class LocationLiveData @Inject constructor(
    private val context: Context,
    private val permissionChecker: PermissionChecker
) : LiveData<Location>() {
    private val locationManager by lazy { context.getSystemService(LOCATION_SERVICE) as LocationManager }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: android.location.Location) {
            value = location.toLocation()
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }
    }

    init {
        if (isLocationPermissionGranted()) {
            onLocationPermissionGranted()
        } else {
            value = null
        }
    }

    override fun onActive() {
        if (isLocationPermissionGranted()) {
//            locationManager.requestLocationUpdates(
//                LocationManager.NETWORK_PROVIDER,
//                0L,
//                0.0f,
//                locationListener
//            )

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0.0f,
                locationListener
            )
        }
    }

    override fun onInactive() {
        locationManager.removeUpdates(locationListener)
    }

    fun isLocationPermissionGranted(): Boolean {
        val checkResult = permissionChecker.check(Manifest.permission.ACCESS_FINE_LOCATION)
        return checkResult == PermissionChecker.CheckResult.GRANTED
    }

    fun onLocationPermissionGranted() {
        if (value != null) {
            return
        }

        val lastKnownLocation = getLastKnownLocation()

        if (lastKnownLocation != null) {
            value = lastKnownLocation
        }

        if (hasActiveObservers()) {
            onActive()
        }
    }

    private fun getLastKnownLocation(): Location? {
        val lastNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val lastGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return lastGpsLocation?.toLocation() ?: lastNetworkLocation?.toLocation()
    }
}