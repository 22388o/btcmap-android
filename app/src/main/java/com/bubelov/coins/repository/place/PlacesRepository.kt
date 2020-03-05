package com.bubelov.coins.repository.place

import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreatePlaceArgs
import com.bubelov.coins.api.coins.UpdatePlaceArgs
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.TableSyncResult
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlacesRepository @Inject constructor(
    private val api: CoinsApi,
    val db: Database,
    private val builtInCache: BuiltInPlacesCache,
    private val userRepository: UserRepository
) {
    private val queries = db.placeQueries

    private val allPlaces = queries.selectAll().asFlow().map { it.executeAsList() }

    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun find(id: String): Place? {
        waitTillCacheIsReady()

        return withContext(Dispatchers.IO) {
            queries.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun findBySearchQuery(searchQuery: String): List<Place> {
        waitTillCacheIsReady()

        return withContext(Dispatchers.IO) {
            queries.selectBySearchQuery(searchQuery).executeAsList()
        }
    }

    suspend fun findRandom() = withContext(Dispatchers.IO) {
        waitTillCacheIsReady()
        queries.selectRandom().executeAsOneOrNull()
    }

//    fun getPlaces(bounds: LatLngBounds): LiveData<List<Place>> =
//        Transformations.switchMap(allPlaces) {
//            MutableLiveData<List<Place>>().apply {
//                value = it.filter { bounds.contains(it.toLatLng()) }
//            }
//        }

    fun getAll(): Flow<List<Place>> {
        return allPlaces;
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val maxUpdatedAt = queries.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getPlaces(
                createdOrUpdatedAfter = DateTime.parse(maxUpdatedAt)
            )

            val newPlaces = response.filter {
                queries.selectById(it.id).executeAsOneOrNull() == null
            }

            queries.transaction {
                response.forEach {
                    queries.insertOrReplace(it)
                }
            }

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

            queries.insertOrReplace(response)
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

            queries.insertOrReplace(response)
            response
        }
    }

    private suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (queries.selectCount().executeAsOne() == 0L) {
                queries.transaction {
                    builtInCache.getPlaces().forEach {
                        queries.insertOrReplace(it)
                    }
                }
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