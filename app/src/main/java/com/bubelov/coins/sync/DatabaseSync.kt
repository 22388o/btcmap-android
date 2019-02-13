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

import com.bubelov.coins.model.SyncLogEntry
import com.bubelov.coins.repository.currency.CurrenciesRepository

import com.bubelov.coins.repository.place.PlacesRepository
import com.bubelov.coins.repository.placecategory.PlaceCategoriesRepository
import com.bubelov.coins.repository.synclogs.SyncLogsRepository
import com.bubelov.coins.util.PlaceNotificationManager

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseSync @Inject constructor(
    private val currenciesRepository: CurrenciesRepository,
    private val placesRepository: PlacesRepository,
    private val placeCategoriesRepository: PlaceCategoriesRepository,
    private val placeNotificationManager: PlaceNotificationManager,
    private val syncLogsRepository: SyncLogsRepository
) {
    suspend fun sync() {
        val currenciesSyncResult = currenciesRepository.sync()
        val placesSyncResult = placesRepository.sync()
        val placeCategoriesSyncResutt = placeCategoriesRepository.sync()

        syncLogsRepository.insert(
            SyncLogEntry(
                System.currentTimeMillis(),
                placesSyncResult.affectedPlaces.size
            )
        )

        placeNotificationManager.issueNotificationsIfNecessary(placesSyncResult.affectedPlaces)
    }
}