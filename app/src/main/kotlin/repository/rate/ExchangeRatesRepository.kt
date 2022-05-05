package repository.rate

import model.CurrencyPair

class ExchangeRatesRepository(
    bitstamp: Bitstamp,
    coinbase: Coinbase
) {
    private val sources = listOf(bitstamp, coinbase)

    fun getExchangeRatesSources(currencyPair: CurrencyPair) =
        sources.filter { it.getCurrencyPairs().contains(currencyPair) }
}