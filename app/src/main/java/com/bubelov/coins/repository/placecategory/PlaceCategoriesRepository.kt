package com.bubelov.coins.repository.placecategory

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.PlaceCategory
import com.bubelov.coins.data.PlaceCategoryQueries
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class PlaceCategoriesRepository(
    private val api: CoinsApi,
    private val db: PlaceCategoryQueries,
    private val builtInCache: BuiltInPlaceCategoriesCache,
    private val logsRepository: LogsRepository
) {

    suspend fun findById(id: String): PlaceCategory? {
        return withContext(Dispatchers.IO) {
            db.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            val maxUpdatedAt = db.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getPlaceCategories(
                createdOrUpdatedAfter = DateTime.parse(maxUpdatedAt)
            )

            db.transaction {
                response.forEach {
                    db.insertOrReplace(it)
                }
            }

            TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = true,
                affectedRecords = response.size
            )
        } catch (t: Throwable) {
            TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = false,
                affectedRecords = 0
            )
        }
    }

    suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (db.selectCount().executeAsOne() == 0L) {
                logsRepository.append(
                    tag = "cache",
                    message = "Initializing built-in place categories cache"
                )

                val placeCategories = builtInCache.placeCategories

                val insertDuration = measureTime {
                    db.transaction {
                        placeCategories.forEach {
                            db.insertOrReplace(it)
                        }
                    }
                }

                logsRepository.append(
                    tag = "cache",
                    message = "Inserted ${placeCategories.size} place categories in ${insertDuration.inMilliseconds.toInt()} ms"
                )
            }
        }
    }
}