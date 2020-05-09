package com.bubelov.coins.repository.place

import android.content.Context
import com.bubelov.coins.data.Place
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
class BuiltInPlacesCache(
    private val context: Context,
    private val gson: Gson,
    private val log: LogsRepository
) {

    val places by lazy {
        loadPlaces()
    }

    private fun loadPlaces(): List<Place> {
        val result: List<Place>
        val fileName = "places.json"

        val duration = measureTime {
            val input = context.assets.open(fileName)
            val typeToken = object : TypeToken<List<Place.Impl>>() {}
            result = gson.fromJson(InputStreamReader(input), typeToken.type)
        }

        log.appendBlocking(
            tag = "cache",
            message = "Parsed $fileName in ${duration.inMilliseconds.toInt()} ms"
        )

        return result
    }
}