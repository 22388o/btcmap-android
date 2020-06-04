package com.bubelov.coins.repository

import com.bubelov.coins.TestSuite
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.rate.Bitstamp
import com.bubelov.coins.repository.rate.Coinbase
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import org.koin.core.inject
import org.koin.test.mock.declareMock
import org.mockito.BDDMockito.*

class ExchangeRatesRepositoryTests : TestSuite() {

    val repository: ExchangeRatesRepository by inject()

    @Test
    fun filtersSources() {
        val bitstamp = declareMock<Bitstamp> {
            given(getCurrencyPairs()).willReturn(emptyList())
        }

        val coinbase = declareMock<Coinbase> {
            given(getCurrencyPairs()).willReturn(listOf(
                CurrencyPair.BTC_GBP,
                CurrencyPair.BTC_EUR
            ))
        }

        val btcEurSources = repository.getExchangeRatesSources(CurrencyPair.BTC_EUR)

        assertEquals(1, btcEurSources.size)
        assertEquals(coinbase, btcEurSources.first())

        verify(bitstamp).getCurrencyPairs()
        verify(coinbase).getCurrencyPairs()

        val btcUsdSources = repository.getExchangeRatesSources(CurrencyPair.BTC_USD)
        Assert.assertTrue(btcUsdSources.isEmpty())

        val btcGbpSources = repository.getExchangeRatesSources(CurrencyPair.BTC_GBP)
        assertEquals(1, btcGbpSources.size)
        assertEquals(coinbase, btcGbpSources.first())
    }
}