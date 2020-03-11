package com.bubelov.coins.repository.currency

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.Currency
import com.bubelov.coins.data.CurrencyQueries
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class CurrenciesRepository(
    private val api: CoinsApi,
    private val db: CurrencyQueries,
    private val builtInCache: BuiltInCurrenciesCache,
    private val logsRepository: LogsRepository
) {

    suspend fun all(): List<Currency> {
        return withContext(Dispatchers.IO) {
            db.selectAll().executeAsList()
        }
    }

    suspend fun find(id: String): Currency? {
        return withContext(Dispatchers.IO) {
            db.selectById(id).executeAsOneOrNull()
        }
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
            val maxUpdatedAt = db.selectMaxUpdatedAt().executeAsOneOrNull()?.MAX
                ?: DateTime(0).toString()

            val response = api.getCurrencies(
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
                    message = "Initializing built-in currencies cache"
                )

                val currencies = builtInCache.getCurrencies()

                val insertDuration = measureTime {
                    db.transaction {
                        currencies.forEach {
                            db.insertOrReplace(it)
                        }
                    }
                }

                logsRepository.append(
                    tag = "cache",
                    message = "Inserted ${currencies.size} currencies in ${insertDuration.inMilliseconds.toInt()} ms"
                )
            }
        }
    }
}