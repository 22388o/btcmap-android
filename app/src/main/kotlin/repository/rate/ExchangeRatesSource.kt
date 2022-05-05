package repository.rate

import model.CurrencyPair
import repository.Result

interface ExchangeRatesSource {

    val name: String

    fun getCurrencyPairs(): Collection<CurrencyPair>

    suspend fun getExchangeRate(pair: CurrencyPair): Result<Double>
}