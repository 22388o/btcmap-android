package com.bubelov.coins.repository.currency

import com.bubelov.coins.Database
import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.Currency
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import timber.log.Timber

class CurrenciesRepository(
    private val api: CoinsApi,
    db: Database,
    private val builtInCache: BuiltInCurrenciesCache
) {
    private val queries = db.currencyQueries;

    private var builtInCacheInitialized = false

    init {
        GlobalScope.launch { initBuiltInCache() }
    }

    suspend fun all(): List<Currency> {
        return withContext(Dispatchers.IO) {
            queries.selectAll().executeAsList()
        }
    }

    suspend fun find(id: String): Currency? {
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

            val response = api.getCurrencies(
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
            if (queries.selectCount().executeAsOne() == 0L) {
                queries.transaction {
                    builtInCache.getCurrencies().forEach {
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