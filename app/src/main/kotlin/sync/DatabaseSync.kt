package sync

import repository.place.PlacesRepository
import notifications.PlaceNotificationManager

class DatabaseSync(
    private val placesRepository: PlacesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
) {

    suspend fun sync() {
        val placesSyncResult = placesRepository.sync()
        placeNotificationManager.issueNotificationsIfInArea(placesSyncResult.newPlaces)
    }
}