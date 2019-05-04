package com.bubelov.coins.repository.currency

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.model.Currency
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrenciesRepository @Inject constructor(
    private val api: CoinsApi,
    private val db: CurrenciesDb,
    private val builtInCache: BuiltInCurrenciesCache
) {
    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun all(): List<Currency> {
        return withContext(Dispatchers.IO) {
            db.all()
        }
    }

    suspend fun find(id: String): Currency? {
        return withContext(Dispatchers.IO) {
            db.find(id)
        }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            waitTillCacheIsReady()

            val response = api.getCurrencies(
                createdOrUpdatedAfter = db.maxUpdatedAt()?.plusMillis(1) ?: DateTime(0)
            )

            db.insert(response)

            TableSyncResult(
                startDate = syncStartDate,
                endDate = DateTime.now(),
                success = true,
                affectedRecords = response.size
            )
        } catch (t: Throwable) {
            Timber.e(t, "Couldn't sync currencies")

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
            if (db.count() == 0) {
                db.insert(builtInCache.getCurrencies())
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