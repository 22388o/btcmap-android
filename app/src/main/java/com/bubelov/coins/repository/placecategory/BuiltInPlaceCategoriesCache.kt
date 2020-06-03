package com.bubelov.coins.repository.placecategory

import android.content.res.AssetManager
import com.bubelov.coins.data.PlaceCategory
import com.bubelov.coins.repository.synclogs.LogsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import kotlin.time.measureTime

class BuiltInPlaceCategoriesCache(
    private val assets: AssetManager,
    private val gson: Gson,
    private val log: LogsRepository
) {

    val placeCategories by lazy {
        loadPlaceCategories()
    }

    private fun loadPlaceCategories(): List<PlaceCategory> {
        val result: List<PlaceCategory>
        val fileName = "place_categories.json"

        val duration = measureTime {
            val input = assets.open(fileName)
            val typeToken = object : TypeToken<List<PlaceCategory.Impl>>() {}
            result = gson.fromJson(InputStreamReader(input), typeToken.type)
        }

        log.appendBlocking(
            tag = "cache",
            message = "Parsed $fileName in ${duration.inMilliseconds.toInt()} ms"
        )

        return result
    }
}