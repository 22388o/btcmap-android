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

package com.bubelov.coins.sync

import com.bubelov.coins.emptyPlace
import com.bubelov.coins.repository.currency.CurrenciesRepository
import com.bubelov.coins.repository.currencyplace.CurrenciesPlacesRepository
import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
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

class DatabaseSyncTest {
    @Mock private lateinit var currenciesRepository: CurrenciesRepository
    @Mock private lateinit var placesRepository: PlacesRepository
    @Mock private lateinit var currenciesPlacesRepository: CurrenciesPlacesRepository
    @Mock private lateinit var placeCategoriesRepository: PlaceCategoriesRepository
    @Mock private lateinit var placeNotificationManager: PlaceNotificationManager
    @Mock private lateinit var syncLogsRepository: SyncLogsRepository
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
            syncLogsRepository
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
        verify(syncLogsRepository).insert(any())
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
        verifyZeroInteractions(syncLogsRepository)
    }
}