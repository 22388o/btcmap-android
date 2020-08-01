package com.bubelov.coins.sync

import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.notifications.PlaceNotificationManager

class DatabaseSync(
    private val placesRepository: PlacesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
    private val log: LogsRepository
) {

    suspend fun sync() {
        val placesSyncResult = placesRepository.sync()
        log += "Got ${placesSyncResult.newPlaces.size} new places"
        placeNotificationManager.issueNotificationsIfInArea(placesSyncResult.newPlaces)
    }
}