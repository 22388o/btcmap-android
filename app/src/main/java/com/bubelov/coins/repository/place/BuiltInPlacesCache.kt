package com.bubelov.coins.repository.place

import android.content.Context
import com.bubelov.coins.data.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuiltInPlacesCache @Inject
internal constructor(private val context: Context, val gson: Gson) {
    fun getPlaces(): List<Place> {
        val input = context.assets.open("places.json")
        val typeToken = object : TypeToken<List<Place>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}