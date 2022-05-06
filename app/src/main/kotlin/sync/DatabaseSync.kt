package sync

import map.PlacesRepository
import etc.PlaceNotificationManager

class DatabaseSync(
    private val placesRepository: PlacesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
) {

    suspend fun sync() {
        placesRepository.sync()
        //placeNotificationManager.issueNotificationsIfInArea(placesSyncResult.newPlaces)
    }
}