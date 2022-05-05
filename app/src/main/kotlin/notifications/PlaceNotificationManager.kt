package notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.bubelov.coins.R
import map.MapFragment
import model.NotificationArea
import repository.area.NotificationAreaRepository
import etc.DistanceUtils
import db.Place
import kotlinx.coroutines.flow.first

class PlaceNotificationManager(
    private val context: Context,
    private val notificationAreaRepository: NotificationAreaRepository
) {

    init {
        registerNotificationChannel()
    }

    suspend fun issueNotificationsIfInArea(newPlaces: Collection<Place>) {
        newPlaces.forEach { place ->
            if (!place.valid) {
                return
            }

            val notificationArea = notificationAreaRepository.getNotificationArea().first()

            if (notificationArea != null && place.inside(notificationArea)) {
                issueNotification(place)
            }
        }
    }

    fun issueNotification(place: Place) {
        val builder = NotificationCompat.Builder(context,
                NEW_PLACE_NOTIFICATIONS_CHANNEL
            )
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