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

package com.bubelov.coins.repository.place

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreatePlaceArgs
import com.bubelov.coins.api.coins.UpdatePlaceArgs
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.TableSyncResult
import com.bubelov.coins.util.toLatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.coroutines.*
import org.joda.time.DateTime
import timber.log.Timber

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val api: CoinsApi,
    private val db: PlacesDb,
    private val builtInCache: BuiltInPlacesCache,
    private val userRepository: UserRepository
) {
    private val allPlaces = db.allAsync()

    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun find(id: Long): Place? {
        waitTillCacheIsReady()

        return withContext(Dispatchers.IO) {
            db.find(id)
        }
    }

    suspend fun findBySearchQuery(searchQuery: String): List<Place> {
        waitTillCacheIsReady()

        return withContext(Dispatchers.IO) {
            db.findBySearchQuery(searchQuery)
        }
    }

    suspend fun findRandom() = withContext(Dispatchers.IO) {
        waitTillCacheIsReady()
        db.findRandom()
    }

    fun getPlaces(bounds: LatLngBounds): LiveData<List<Place>> =
        Transformations.switchMap(allPlaces) {
            MutableLiveData<List<Place>>().apply {
                value = it.filter { bounds.contains(it.toLatLng()) }
            }
        }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val request = api.getPlaces(
                db.maxUpdatedAt()?.plusMillis(1) ?: DateTime(0),
                Integer.MAX_VALUE
            )

            val response = request.await()
            val newPlaces = response.filter { db.find(it.id) == null }
            db.insert(response)

            val tableSyncResult = TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = true,
                affectedRecords = response.size
            )

            PlacesSyncResult(tableSyncResult, newPlaces)
        } catch (t: Throwable) {
            Timber.e(t, "Couldn't sync places")

            val tableSyncResult = TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = false,
                affectedRecords = 0
            )

            PlacesSyncResult(tableSyncResult, emptyList())
        }
    }

    suspend fun addPlace(place: Place): Place {
        return withContext(Dispatchers.IO) {
            val request = api.createPlace(
                authorization = userRepository.getAuthorization(),
                args = CreatePlaceArgs(place)
            )

            val result = request.await()
            db.insert(listOf(result))
            result
        }
    }

    suspend fun updatePlace(place: Place): Place {
        return withContext(Dispatchers.IO) {
            val request = api.updatePlace(
                id = place.id,
                authorization = userRepository.getAuthorization(),
                args = UpdatePlaceArgs(place)
            )

            val result = request.await()
            db.update(result)
            result
        }
    }

    private suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (db.count() == 0) {
                db.insert(builtInCache.getPlaces())
            }

            builtInCacheInitialized = true
        }
    }

    private suspend fun waitTillCacheIsReady() {
        while (!builtInCacheInitialized) {
            delay(100)
        }
    }

    data class PlacesSyncResult(
        val tableSyncResult: TableSyncResult,
        val newPlaces: List<Place>
    )
}