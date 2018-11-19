/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

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
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

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
    fun setup() {
        MockitoAnnotations.initMocks(this)

        model = SettingsViewModel(
            placesRepository,
            distanceUnitsLiveData,
            databaseSync,
            syncLogsRepository,
            notificationManager,
            Dispatchers.Default
        )
    }

    @Test
    fun callsSync() = runBlocking {
        model.syncDatabase().join()
        verify(databaseSync).sync()
    }

    @Test
    fun returnsSyncLogs() = runBlocking {
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
                id = 1,
                name = "Random Place"
            )
        )

        model.testNotification().join()

        verify(placesRepository).findRandom()
        verifyNoMoreInteractions(placesRepository)
    }
}