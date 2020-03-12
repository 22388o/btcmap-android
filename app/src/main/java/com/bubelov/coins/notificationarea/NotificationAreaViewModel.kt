package com.bubelov.coins.notificationarea

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.bubelov.coins.R
import com.bubelov.coins.model.Location
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import com.bubelov.coins.repository.placeicon.PlaceIconsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlin.math.ln

@ExperimentalCoroutinesApi
class NotificationAreaViewModel(
    private val areaRepository: NotificationAreaRepository,
    private val placeIconsRepository: PlaceIconsRepository,
    private val defaultLocation: Location
) : ViewModel() {

    suspend fun getNotificationArea(): NotificationArea {
        return areaRepository.getNotificationArea().first() ?: NotificationArea(
            defaultLocation.latitude,
            defaultLocation.longitude,
            NotificationAreaRepository.DEFAULT_RADIUS_METERS
        )
    }

    suspend fun setNotificationArea(notificationArea: NotificationArea) {
        areaRepository.setNotificationArea(notificationArea)
    }

    fun getZoomLevel(area: Double): Int {
        val scale = area / 500
        return (16 - ln(scale) / ln(2.0)).toInt()
    }

    fun getPinIcon(): Bitmap {
        return placeIconsRepository.createMarker(R.drawable.ic_touch)
    }
}