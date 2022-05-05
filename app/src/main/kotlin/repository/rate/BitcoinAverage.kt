package repository.rate

import api.rates.BitcoinAverageApi
import model.CurrencyPair
import repository.Result
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BitcoinAverage (gson: Gson) : ExchangeRatesSource {

    override val name = "BitcoinAverage"

    val api: BitcoinAverageApi = Retrofit.Builder()
        .baseUrl("https://apiv2.bitcoinaverage.com/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(BitcoinAverageApi::class.java)

    override fun getCurrencyPairs(): Collection<CurrencyPair> {
        return listOf(CurrencyPair.BTC_USD)
    }

    override suspend fun getExchangeRate(pair: CurrencyPair): Result<Double> {
        if (pair == CurrencyPair.BTC_USD) {
            return try {
                val response = api.getUsdTicker()
                Result.Success(response.btcUsd.last)
            } catch (e: Exception) {
                Result.Error(e)
            }
        } else {
            throw IllegalArgumentException()
        }
    }
}