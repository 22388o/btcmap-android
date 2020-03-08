package com.bubelov.coins.repository.place

import android.content.Context
import com.bubelov.coins.data.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class BuiltInPlacesCache(
    private val context: Context,
    val gson: Gson
) {

    fun getPlaces(): List<Place> {
        val input = context.assets.open("places.json")
        val typeToken = object : TypeToken<List<Place.Impl>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}