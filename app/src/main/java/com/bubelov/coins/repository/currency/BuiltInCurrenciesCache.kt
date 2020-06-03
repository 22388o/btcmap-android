package com.bubelov.coins.repository.currency

import android.content.res.AssetManager
import com.bubelov.coins.data.Currency
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class BuiltInCurrenciesCache(
    private val assets: AssetManager,
    private val gson: Gson,
    private val log: LogsRepository
) {

    val currencies by lazy {
        parseCurrencies()
    }

    private fun parseCurrencies(): List<Currency> {
        val result: List<Currency>
        val fileName = "currencies.json"

        val duration = measureTime {
            val input = assets.open(fileName)
            val typeToken = object : TypeToken<List<Currency.Impl>>() {}
            result = gson.fromJson(InputStreamReader(input), typeToken.type)
        }

        log.appendBlocking(
            tag = "cache",
            message = "Parsed $fileName in ${duration.inMilliseconds.toInt()} ms"
        )

        return result
    }
}