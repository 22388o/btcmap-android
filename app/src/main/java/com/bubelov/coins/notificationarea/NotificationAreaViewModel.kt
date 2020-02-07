package com.bubelov.coins.notificationarea

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bubelov.coins.R
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import javax.inject.Inject
import javax.inject.Named

class NotificationAreaViewModel @Inject constructor(
    private val areaRepository: NotificationAreaRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    @Named("default_location")
    private val defaultLocation: Location
) : ViewModel() {

    fun getNotificationArea(): NotificationArea {
        return areaRepository.notificationArea ?: NotificationArea(
            defaultLocation.latitude,
            defaultLocation.longitude,
            NotificationAreaRepository.DEFAULT_RADIUS_METERS
        )
    }

    fun setNotificationArea(notificationArea: NotificationArea) {
        areaRepository.notificationArea = notificationArea
    }

    fun getZoomLevel(area: Double): Int {
        val scale = area / 500
        return (16 - Math.log(scale) / Math.log(2.0)).toInt()
    }

    fun getPinIcon(): Bitmap {
        return placeIconsRepository.createMarker(R.drawable.ic_touch)
    }
}