package com.bubelov.coins.settings

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.bubelov.coins.data.LogEntry
import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.sync.DatabaseSync
import com.bubelov.coins.util.DistanceUnitsLiveData
import com.bubelov.coins.util.PlaceNotificationManager
import com.bubelov.coins.util.blockingObserve
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.joda.time.DateTime
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import java.util.*
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
class SettingsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock private lateinit var distanceUnitsLiveData: DistanceUnitsLiveData
    @Mock private lateinit var databaseSync: DatabaseSync
    @Mock private lateinit var logsRepository: LogsRepository
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
            logsRepository,
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

        val logs = listOf(LogEntry.Impl(
            datetime = DateTime.now().toString(),
            tag = "test_tag",
            message = "test_message"
        ))

        whenever(logsRepository.getAll()).thenReturn(flowOf(logs))
        model.showSyncLogs().join()
        assertEquals(1, model.syncLogs.blockingObserve().size)
        verify(logsRepository).getAll()
        verifyNoMoreInteractions(logsRepository)
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