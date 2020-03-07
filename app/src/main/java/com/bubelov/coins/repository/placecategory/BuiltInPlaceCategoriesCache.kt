package com.bubelov.coins.repository.placecategory

import android.content.Context
import com.bubelov.coins.data.PlaceCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class BuiltInPlaceCategoriesCache(
    private val context: Context,
    val gson: Gson
) {

    fun getPlaceCategories(): List<PlaceCategory> {
        val input = context.assets.open("place_categories.json")
        val typeToken = object : TypeToken<List<PlaceCategory>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}