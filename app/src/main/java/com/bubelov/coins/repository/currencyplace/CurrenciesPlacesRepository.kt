package com.bubelov.coins.repository.currencyplace

import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesPlacesRepository @Inject constructor(
    private val api: CoinsApi,
    val db: Database,
    private val builtInCache: BuiltInCurrenciesPlacesCache
) {
    private val queries = db.currencyPlaceQueries

    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun findByPlaceId(placeId: String) = withContext(Dispatchers.IO) {
        waitTillCacheIsReady()
        queries.selectByPlaceId(placeId).executeAsList()
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val maxUpdatedAt = queries.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getCurrenciesPlaces(
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
            Timber.e(t, "Couldn't sync currencies to places mapping")

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
                    builtInCache.getCurrenciesPlaces().forEach {
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