package com.bubelov.coins.sync

import com.bubelov.coins.model.SyncLogEntry
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
import com.bubelov.coins.util.PlaceNotificationManager

class DatabaseSync(
    private val currenciesRepository: CurrenciesRepository,
    private val placesRepository: PlacesRepository,
    private val currenciesPlacesRepository: CurrenciesPlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
    private val syncLogsRepository: SyncLogsRepository
) {
    suspend fun sync() {
        val currenciesSyncResult = currenciesRepository.sync()
        val placesSyncResult = placesRepository.sync()
        val currenciesPlacesSyncResult = currenciesPlacesRepository.sync()
        val placeCategoriesSyncResutt = placeCategoriesRepository.sync()

        syncLogsRepository.insert(
            SyncLogEntry(
                System.currentTimeMillis(),
                placesSyncResult.newPlaces.size
            )
        )

        placeNotificationManager.issueNotificationsIfInArea(placesSyncResult.newPlaces)
    }
}