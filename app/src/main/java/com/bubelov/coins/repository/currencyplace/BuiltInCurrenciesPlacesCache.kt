package com.bubelov.coins.repository.currencyplace

import android.content.Context
import com.bubelov.coins.data.CurrencyPlace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class BuiltInCurrenciesPlacesCache(
    private val context: Context,
    val gson: Gson
) {
    fun getCurrenciesPlaces(): List<CurrencyPlace> {
        val input = context.assets.open("currencies_places.json")
        val typeToken = object : TypeToken<List<CurrencyPlace.Impl>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}