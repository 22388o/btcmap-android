package com.bubelov.coins.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.TestSuite
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.notifications.PlaceNotificationManager
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.sync.DatabaseSync
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import java.util.*
import org.mockito.BDDMockito.*

class SettingsViewModelTests : TestSuite() {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val model: SettingsViewModel by inject()

    @Test
    fun callsSync() = runBlocking {
        val databaseSync = declareMock<DatabaseSync>()
        model.syncDatabase()
        verify(databaseSync).sync()
    }

//    @Test
//    fun returnsSyncLogs() = runBlocking<Unit> {
//        val logs = listOf(
//            LogEntry(
//                datetime = DateTime.now().toString(),
//                tag = "test_tag",
//                message = "test_message"
//            )
//        )
//
//        val logsRepository = declareMock<LogsRepository> {
//            given(getAll()).willReturn(flowOf(logs))
//        }
//
//        model.showSyncLogs().join()
//        assertEquals(1, model.syncLogs.blockingObserve().size)
//        verify(logsRepository).getAll()
//    }

    @Test
    fun sendsRandomPlaceNotification() = runBlocking {
        val place = emptyPlace().copy(
            id = UUID.randomUUID().toString(),
            name = "Random Place"
        )

        val placesRepository = declareMock<PlacesRepository> {
            given(findRandom()).willReturn(place)
        }

        declareMock<PlaceNotificationManager>()

        model.testNotification()

        verify(placesRepository).findRandom()
        verifyNoMoreInteractions(placesRepository)
    }
}