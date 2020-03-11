package com.bubelov.coins.sync

import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.util.PlaceNotificationManager
import com.bubelov.coins.util.TableSyncResult
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.runBlocking
import org.joda.time.DateTime
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DatabaseSyncTest {

    @Mock private lateinit var currenciesRepository: CurrenciesRepository
    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var currenciesPlacesRepository: CurrenciesPlacesRepository
    @Mock private lateinit var placeCategoriesRepository: PlaceCategoriesRepository
    @Mock private lateinit var placeNotificationManager: PlaceNotificationManager
    @Mock private lateinit var logsRepository: LogsRepository

    private lateinit var databaseSync: DatabaseSync

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        databaseSync = DatabaseSync(
            currenciesRepository,
            placesRepository,
            currenciesPlacesRepository,
            placeCategoriesRepository,
            placeNotificationManager,
            logsRepository
        )
    }

    @Test
    fun handleSuccessfulSync() = runBlocking {
        val newPlaces = listOf(
            emptyPlace().copy(id = UUID.randomUUID().toString()),
            emptyPlace().copy(id = UUID.randomUUID().toString()),
            emptyPlace().copy(id = UUID.randomUUID().toString())
        )

        whenever(placesRepository.sync()).thenReturn(
            PlacesRepository.PlacesSyncResult(
                tableSyncResult = TableSyncResult(
                    startDate = DateTime.now(),
                    endDate = DateTime.now(),
                    success = true,
                    affectedRecords = newPlaces.size
                ),
                newPlaces = newPlaces
            )
        )

        databaseSync.sync()

        verify(placeNotificationManager).issueNotificationsIfInArea(newPlaces)
        verify(logsRepository).append(any(), any())
    }

    @Test
    fun handleFailedFetch() = runBlocking {
        whenever(placesRepository.sync()).thenThrow(IllegalStateException())

        try {
            databaseSync.sync()
            assertFalse(true)
        } catch (e: IllegalStateException) {
        }

        verifyZeroInteractions(placeNotificationManager)
        verifyZeroInteractions(logsRepository)
    }
}