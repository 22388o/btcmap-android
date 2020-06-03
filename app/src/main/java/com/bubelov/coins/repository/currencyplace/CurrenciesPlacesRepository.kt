package com.bubelov.coins.repository.currencyplace

import com.bubelov.coins.api.coins.CoinsApi
import com.bubelov.coins.data.CurrencyPlaceQueries
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.bubelov.coins.util.TableSyncResult
import kotlinx.coroutines.*
import org.joda.time.DateTime
import kotlin.time.measureTime

class CurrenciesPlacesRepository(
    private val api: CoinsApi,
    private val db: CurrencyPlaceQueries,
    private val builtInCache: BuiltInCurrenciesPlacesCache,
    private val logsRepository: LogsRepository
) {

    suspend fun findByPlaceId(placeId: String) = withContext(Dispatchers.IO) {
        db.selectByPlaceId(placeId).executeAsList()
    }

    suspend fun sync() = withContext(Dispatchers.IO) {
        val syncStartDate = DateTime.now()

        try {
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

    suspend fun initBuiltInCache() {
        withContext(Dispatchers.IO) {
            if (db.selectCount().executeAsOne() == 0L) {
                logsRepository.append(
                    tag = "cache",
                    message = "Initializing built-in currencies-places cache"
                )

                val currenciesPlaces = builtInCache.currenciesPlaces

                val insertDuration = measureTime {
                    db.transaction {
                        currenciesPlaces.forEach {
                            db.insertOrReplace(it)
                        }
                    }
                }

                logsRepository.append(
                    tag = "cache",
                    message = "Inserted ${currenciesPlaces.size} currencies-places in ${insertDuration.inMilliseconds.toInt()} ms"
                )
            }
        }
    }
}