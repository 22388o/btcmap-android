package com.bubelov.coins.repository.place

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreatePlaceArgs
import com.bubelov.coins.api.coins.UpdatePlaceArgs
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
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

    suspend fun find(id: String): Place? {
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

//    fun getPlaces(bounds: LatLngBounds): LiveData<List<Place>> =
//        Transformations.switchMap(allPlaces) {
//            MutableLiveData<List<Place>>().apply {
//                value = it.filter { bounds.contains(it.toLatLng()) }
//            }
//        }

    fun getAll(): Flow<List<Place>> {
        return db.allAsFlow()
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val response = api.getPlaces(
                db.maxUpdatedAt()?.plusMillis(1) ?: DateTime(0)
            )

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
            val response = api.addPlace(
                authorization = "Bearer ${userRepository.getToken()}",
                args = CreatePlaceArgs(place)
            )

            db.insert(listOf(response))
            response
        }
    }

    suspend fun updatePlace(place: Place): Place {
        return withContext(Dispatchers.IO) {
            val response = api.updatePlace(
                id = place.id,
                authorization = "Bearer ${userRepository.getToken()}",
                args = UpdatePlaceArgs(place)
            )

            db.update(response)
            response
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