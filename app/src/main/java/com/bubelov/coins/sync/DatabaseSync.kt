package com.bubelov.coins.sync

import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.notifications.PlaceNotificationManager
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DatabaseSync(
    private val currenciesRepository: CurrenciesRepository,
    private val placesRepository: PlacesRepository,
    private val currenciesPlacesRepository: CurrenciesPlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
    private val logsRepository: LogsRepository
) {
    suspend fun sync() {
        val currenciesSyncResult = currenciesRepository.sync()
        val placesSyncResult = placesRepository.sync()
        val currenciesPlacesSyncResult = currenciesPlacesRepository.sync()
        val placeCategoriesSyncResult = placeCategoriesRepository.sync()

        logsRepository.append(
            tag = "database_sync",
            message = "Currencies sync: ${currenciesSyncResult.success}"
        )

        logsRepository.append(
            tag = "database_sync",
            message = "Got ${placesSyncResult.newPlaces.size} new places"
        )

        logsRepository.append(
            tag = "database_sync",
            message = "CurrenciesPlaces sync: ${currenciesPlacesSyncResult.success}"
        )

        logsRepository.append(
            tag = "database_sync",
            message = "Place categories sync: ${placeCategoriesSyncResult.success}"
        )

        placeNotificationManager.issueNotificationsIfInArea(placesSyncResult.newPlaces)
    }
}