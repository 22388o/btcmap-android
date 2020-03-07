package com.bubelov.coins.repository.placecategory

import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.PlaceCategory
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import timber.log.Timber

class PlaceCategoriesRepository(
    private val api: CoinsApi,
    val db: Database,
    private val builtInCache: BuiltInPlaceCategoriesCache
) {

    private val queries = db.placeCategoryQueries

    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun findById(id: String): PlaceCategory? {
        return withContext(Dispatchers.IO) {
            queries.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val maxUpdatedAt = queries.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getPlaceCategories(
                createdOrUpdatedAfter = DateTime.parse(maxUpdatedAt)
            )

            queries.transaction {
                response.forEach {
                    queries.insertOrReplace(it)
                }
            }

            TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = true,
                affectedRecords = response.size
            )
        } catch (t: Throwable) {
            Timber.e(t, "Couldn't sync place categories")

            TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = false,
                affectedRecords = 0
            )
        }
    }

    private suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (queries.selectCount().executeAsOne() == 0L) {
                queries.transaction {
                    builtInCache.getPlaceCategories().forEach {
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
}