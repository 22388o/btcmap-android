package com.bubelov.coins.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.model.SyncLogEntry
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.util.DistanceUnitsLiveData
import com.bubelov.coins.util.PlaceNotificationManager
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*

@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var distanceUnitsLiveData: DistanceUnitsLiveData
    @Mock private lateinit var databaseSync: DatabaseSync
    @Mock private lateinit var syncLogsRepository: SyncLogsRepository
    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var notificationManager: PlaceNotificationManager

    private lateinit var model: SettingsViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        model = SettingsViewModel(
            placesRepository,
            distanceUnitsLiveData,
            databaseSync,
            syncLogsRepository,
            notificationManager
        )
    }

    @Test
    fun callsSync() = runBlocking {
        model.syncDatabase()
        verify(databaseSync).sync()
    }

    @Test
    fun returnsSyncLogs() = runBlocking {
        Dispatchers.setMain(Dispatchers.Default)
        val logs = listOf(SyncLogEntry(0, 10))
        whenever(syncLogsRepository.all()).thenReturn(logs)
        model.showSyncLogs().join()
        assertEquals(1, model.syncLogs.blockingObserve().size)
        verify(syncLogsRepository).all()
        verifyNoMoreInteractions(syncLogsRepository)
    }

    @Test
    fun sendsRandomPlaceNotification() = runBlocking {
        whenever(placesRepository.findRandom()).thenReturn(
            emptyPlace().copy(
                id = UUID.randomUUID().toString(),
                name = "Random Place"
            )
        )

        model.testNotification()

        verify(placesRepository).findRandom()
        verifyNoMoreInteractions(placesRepository)
    }
}