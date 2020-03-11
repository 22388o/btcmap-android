package com.bubelov.coins.repository.currencyplace

import android.content.Context
import com.bubelov.coins.data.CurrencyPlace
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class BuiltInCurrenciesPlacesCache(
    private val context: Context,
    private val gson: Gson,
    private val logsRepository: LogsRepository
) {
    fun getCurrenciesPlaces(): List<CurrencyPlace> {
        val result: List<CurrencyPlace>
        val fileName = "currencies_places.json"

        val duration = measureTime {
            val input = context.assets.open(fileName)
            val typeToken = object : TypeToken<List<CurrencyPlace.Impl>>() {}
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