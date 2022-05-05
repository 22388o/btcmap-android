package repository.place

import android.util.Log
import api.coins.CoinsApi
import api.coins.CreatePlaceArgs
import api.coins.UpdatePlaceArgs
import repository.synclogs.LogsRepository
import repository.user.UserRepository
import etc.TableSyncResult
import com.squareup.sqldelight.runtime.coroutines.asFlow
import db.Place
import db.PlaceQueries
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.lang.Exception
import java.time.LocalDateTime

class PlacesRepository(
    private val api: CoinsApi,
    private val db: PlaceQueries,
    private val builtInCache: BuiltInPlacesCache,
    private val userRepository: UserRepository,
    private val log: LogsRepository
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

    fun get(
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double
    ): Flow<List<Place>> {
        return db.selectByBoundingBox(
            minLat = minLat,
            maxLat = maxLat,
            minLon = minLon,
            maxLon = maxLon
        ).asFlow().map { it.executeAsList() }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        initBuiltInCache()

        val syncStartDate = LocalDateTime.now()

        try {
            val maxUpdatedAt = db.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: LocalDateTime.now().minusYears(50).toString()

            val response = api.getPlaces(
                createdOrUpdatedAfter = LocalDateTime.parse(maxUpdatedAt)
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
                endDate = LocalDateTime.now(),
                success = true,
                affectedRecords = response.size
            )

            PlacesSyncResult(tableSyncResult, newPlaces)
        } catch (e: Exception) {
            Log.e("PlacesRepository", "Sync failed", e)

            val tableSyncResult = TableSyncResult(
                startDate = syncStartDate,
                endDate = LocalDateTime.now(),
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

            log += "Initializing built-in places cache"

            val places = builtInCache.loadPlaces()

            db.transaction {
                places.forEach {
                    db.insertOrReplace(it)
                }
            }
        }
    }

    data class PlacesSyncResult(
        val tableSyncResult: TableSyncResult,
        val newPlaces: List<Place>
    )
}