package com.bubelov.coins.repository.rate

import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result

interface ExchangeRatesSource {

    val name: String

    fun getCurrencyPairs(): Collection<CurrencyPair>

    suspend fun getExchangeRate(pair: CurrencyPair): Result<Double>
}