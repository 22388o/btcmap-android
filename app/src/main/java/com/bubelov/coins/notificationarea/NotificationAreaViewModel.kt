package com.bubelov.coins.notificationarea

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bubelov.coins.R
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import kotlin.math.ln

class NotificationAreaViewModel(
    private val areaRepository: NotificationAreaRepository,
    private val placeIconsRepository: PlaceIconsRepository,
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
        return (16 - ln(scale) / ln(2.0)).toInt()
    }

    fun getPinIcon(): Bitmap {
        return placeIconsRepository.createMarker(R.drawable.ic_touch)
    }
}