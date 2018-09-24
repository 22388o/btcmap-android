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

package com.bubelov.coins.notificationarea

import android.arch.lifecycle.ViewModel
import com.bubelov.coins.BuildConfig
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.repository.area.NotificationAreaRepository
import javax.inject.Inject

class NotificationAreaViewModel @Inject constructor(
    private val areaRepository: NotificationAreaRepository
) : ViewModel() {

    fun getNotificationArea(): NotificationArea {
        return areaRepository.notificationArea ?: NotificationArea(
            BuildConfig.DEFAULT_LOCATION_LAT,
            BuildConfig.DEFAULT_LOCATION_LON,
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
}