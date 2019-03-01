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

package com.bubelov.coins.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bubelov.coins.R
import com.bubelov.coins.map.MapFragment
import com.bubelov.coins.model.NotificationArea
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.area.NotificationAreaRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceNotificationManager @Inject
internal constructor(
    private val context: Context,
    private val notificationAreaRepository: NotificationAreaRepository
) {
    init {
        registerNotificationChannel()
    }

    fun issueNotificationsIfInArea(newPlaces: Collection<Place>) {
        newPlaces.forEach { place ->
            if (!place.visible) {
                return
            }

            val notificationArea = notificationAreaRepository.notificationArea

            if (notificationArea != null && place.inside(notificationArea)) {
                issueNotification(place)
            }
        }
    }

    fun issueNotification(place: Place) {
        val builder = NotificationCompat.Builder(context, NEW_PLACE_NOTIFICATIONS_CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_new_place))
            .setContentText(place.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.mapFragment)
            .setArguments(MapFragment.newOpenPlaceArguments(place))
            .createPendingIntent()

        builder.setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(place.id.hashCode(), builder.build())
    }

    private fun registerNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.new_place_notifications)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(NEW_PLACE_NOTIFICATIONS_CHANNEL, name, importance)

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun Place.inside(area: NotificationArea): Boolean {
        return DistanceUtils.getDistance(
            area.latitude,
            area.longitude,
            latitude,
            longitude
        ) <= area.radius
    }

    companion object {
        private const val NEW_PLACE_NOTIFICATIONS_CHANNEL = "newPlaceNotifications"
    }
}