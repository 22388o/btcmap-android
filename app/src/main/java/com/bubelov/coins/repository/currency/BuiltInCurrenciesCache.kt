package com.bubelov.coins.repository.currency

import android.content.Context
import com.bubelov.coins.data.Currency
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class BuiltInCurrenciesCache(
    private val context: Context,
    private val gson: Gson,
    private val logsRepository: LogsRepository
) {
    fun getCurrencies(): List<Currency> {
        val result: List<Currency>
        val fileName = "currencies.json"

        val duration = measureTime {
            val input = context.assets.open(fileName)
            val typeToken = object : TypeToken<List<Currency.Impl>>() {}
            result = gson.fromJson(InputStreamReader(input), typeToken.type)
        }

        GlobalScope.launch {
            logsRepository.append(
                tag = "cache",
                message = "Parsed $fileName in ${duration.inMilliseconds.toInt()} ms"
            )
        }

        return result
    }
}