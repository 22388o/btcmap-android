package com.bubelov.coins.repository.currency

import android.content.Context
import com.bubelov.coins.data.Currency
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BuiltInCurrenciesCache @Inject constructor(
    private val context: Context,
    val gson: Gson
) {
    fun getCurrencies(): List<Currency> {
        val input = context.assets.open("currencies.json")
        val typeToken = object : TypeToken<List<Currency>>() {}
        return gson.fromJson(InputStreamReader(input), typeToken.type)
    }
}