package com.bubelov.coins.repository.rate

import com.bubelov.coins.api.rates.CoinbaseApi
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Coinbase(gson: Gson) : ExchangeRatesSource {
    override val name = "Coinbase"

    val api: CoinbaseApi = Retrofit.Builder()
        .baseUrl("https://api.coinbase.com/v2/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(CoinbaseApi::class.java)

    override fun getCurrencyPairs(): Collection<CurrencyPair> {
        return listOf(CurrencyPair.BTC_USD, CurrencyPair.BTC_EUR, CurrencyPair.BTC_GBP)
    }

    override suspend fun getExchangeRate(pair: CurrencyPair): Result<Double> {
        if (pair == CurrencyPair.BTC_USD || pair == CurrencyPair.BTC_EUR || pair == CurrencyPair.BTC_GBP) {
            return try {
                val result = api.getExchangeRates()
                Result.Success(result.data.rates.getValue(pair.quoteCurrency))
            } catch (e: Exception) {
                Result.Error(e)
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}