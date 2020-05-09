package com.bubelov.coins.repository.place

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.api.coins.CreatePlaceArgs
import com.bubelov.coins.api.coins.UpdatePlaceArgs
import com.bubelov.coins.data.Place
import com.bubelov.coins.data.PlaceQueries
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.repository.user.UserRepository
import com.bubelov.coins.util.TableSyncResult
import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.time.DateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class PlacesRepository(
    private val api: CoinsApi,
    private val db: PlaceQueries,
    private val builtInCache: BuiltInPlacesCache,
    private val userRepository: UserRepository,
    private val logsRepository: LogsRepository
) {

    suspend fun find(id: String): Place? {
        return withContext(Dispatchers.IO) {
            db.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun findBySearchQuery(searchQuery: String): List<Place> {
        return withContext(Dispatchers.IO) {
            db.selectBySearchQuery(searchQuery).executeAsList()
        }
    }

    suspend fun findRandom() = withContext(Dispatchers.IO) {
        db.selectRandom().executeAsOneOrNull()
    }

    fun getAll(): Flow<List<Place>> {
        return db.selectAll().asFlow().map { it.executeAsList() }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            val maxUpdatedAt = db.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getPlaces(
                createdOrUpdatedAfter = DateTime.parse(maxUpdatedAt)
            )

            val newPlaces = response.filter {
                db.selectById(it.id).executeAsOneOrNull() == null
            }

            db.transaction {
                response.forEach {
                    db.insertOrReplace(it)
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

            db.insertOrReplace(response)
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

            db.insertOrReplace(response)
            response
        }
    }

    suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            val empty = db.selectCount().executeAsOne() == 0L

            if (!empty) {
                return@withContext
            }

            logsRepository.append(
                tag = "cache",
                message = "Initializing built-in places cache"
            )

            val places = builtInCache.places

            val insertDuration = measureTime {
                db.transaction {
                    places.forEach {
                        db.insertOrReplace(it)
                    }
                }
            }

            logsRepository.append(
                tag = "cache",
                message = "Inserted ${places.size} places in ${insertDuration.inMilliseconds.toInt()} ms"
            )
        }
    }

    data class PlacesSyncResult(
        val tableSyncResult: TableSyncResult,
        val newPlaces: List<Place>
    )
}