package com.bubelov.coins.repository.currencyplace

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.CurrencyPlaceQueries
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime

class CurrenciesPlacesRepository(
    private val api: CoinsApi,
    private val db: CurrencyPlaceQueries,
    private val builtInCache: BuiltInCurrenciesPlacesCache
) {

    private var builtInCacheInitialized = false

    init {
        runBlocking {
            initBuiltInCache()
        }
    }

    suspend fun findByPlaceId(placeId: String) = withContext(Dispatchers.IO) {
        waitTillCacheIsReady()
        db.selectByPlaceId(placeId).executeAsList()
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val maxUpdatedAt = db.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getCurrenciesPlaces(
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

    private suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (db.selectCount().executeAsOne() == 0L) {
                db.transaction {
                    builtInCache.getCurrenciesPlaces().forEach {
                        db.insertOrReplace(it)
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