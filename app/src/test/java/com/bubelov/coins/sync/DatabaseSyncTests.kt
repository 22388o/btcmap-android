package com.bubelov.coins.sync

import com.bubelov.coins.TestSuite
import com.bubelov.coins.notifications.PlaceNotificationManager
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class DatabaseSyncTests : TestSuite() {

    private val databaseSync: DatabaseSync by inject()

//    @Test
//    fun handleSuccessfulSync() = runBlocking {
//        val newPlaces = listOf(
//            emptyPlace().copy(id = UUID.randomUUID().toString()),
//            emptyPlace().copy(id = UUID.randomUUID().toString()),
//            emptyPlace().copy(id = UUID.randomUUID().toString())
//        )
//
//        whenever(placesRepository.sync()).thenReturn(
//            PlacesRepository.PlacesSyncResult(
//                tableSyncResult = TableSyncResult(
//                    startDate = DateTime.now(),
//                    endDate = DateTime.now(),
//                    success = true,
//                    affectedRecords = newPlaces.size
//                ),
//                newPlaces = newPlaces
//            )
//        )
//
//        databaseSync.sync()
//
//        verify(placeNotificationManager).issueNotificationsIfInArea(newPlaces)
//        verify(logsRepository).append(any(), any())
//    }

    @Test
    fun handleFailedFetch() = runBlocking {
        declareMock<PlacesRepository> {
            given(sync()).willThrow(IllegalStateException())
        }

        val placeNotificationManager = declareMock<PlaceNotificationManager>()
        val logsRepository = declareMock<LogsRepository>()

        try {
            databaseSync.sync()
            assertFalse(true)
        } catch (e: IllegalStateException) {
        }

        verifyNoInteractions(placeNotificationManager)
    }
}