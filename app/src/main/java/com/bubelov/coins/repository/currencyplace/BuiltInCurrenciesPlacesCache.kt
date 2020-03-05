package com.bubelov.coins.repository.currencyplace

import android.content.Context
import com.bubelov.coins.data.CurrencyPlace
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuiltInCurrenciesPlacesCache @Inject constructor(
    private val context: Context,
    val gson: Gson
) {
    fun getCurrenciesPlaces(): List<CurrencyPlace> {
        val input = context.assets.open("currencies_places.json")
        val typeToken = object : TypeToken<List<CurrencyPlace>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}