package com.bubelov.coins.repository.rate

import com.bubelov.coins.api.rates.BitstampApi
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Bitstamp(gson: Gson) : ExchangeRatesSource {

    override val name = "Bitstamp"

    val api: BitstampApi = Retrofit.Builder()
        .baseUrl("https://www.bitstamp.net/api/v2/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(BitstampApi::class.java)

    override fun getCurrencyPairs(): Collection<CurrencyPair> {
        return listOf(CurrencyPair.BTC_USD, CurrencyPair.BTC_EUR)
    }

    override suspend fun getExchangeRate(pair: CurrencyPair): Result<Double> {
        return try {
            when (pair) {
                CurrencyPair.BTC_USD -> Result.Success(api.getBtcUsdTicker().last.toDouble())
                CurrencyPair.BTC_EUR -> Result.Success(api.getBtcEurTicker().last.toDouble())
                else -> throw IllegalArgumentException()
            }
        } catch (t: Throwable) {
            Result.Error(t)
        }
    }
}