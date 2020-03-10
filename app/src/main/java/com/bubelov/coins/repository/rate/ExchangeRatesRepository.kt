package com.bubelov.coins.repository.rate

import com.bubelov.coins.model.CurrencyPair

class ExchangeRatesRepository(
    bitstamp: Bitstamp,
    coinbase: Coinbase
) {
    private val sources = listOf(bitstamp, coinbase)

    fun getExchangeRatesSources(currencyPair: CurrencyPair) =
        sources.filter { it.getCurrencyPairs().contains(currencyPair) }
}