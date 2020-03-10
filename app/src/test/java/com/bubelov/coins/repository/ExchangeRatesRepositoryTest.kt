package com.bubelov.coins.repository

import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.rate.Bitstamp
import com.bubelov.coins.repository.rate.Coinbase
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ExchangeRatesRepositoryTest {

    @Mock private lateinit var bitstamp: Bitstamp
    @Mock private lateinit var coinbase: Coinbase

    private lateinit var repository: ExchangeRatesRepository

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)

        repository = ExchangeRatesRepository(
            bitstamp,
            coinbase
        )
    }

    @Test
    fun filtersSources() {
        whenever(bitstamp.getCurrencyPairs()).thenReturn(emptyList())

        whenever(coinbase.getCurrencyPairs()).thenReturn(listOf(
            CurrencyPair.BTC_GBP,
            CurrencyPair.BTC_EUR
        ))

        val btcEurSources = repository.getExchangeRatesSources(CurrencyPair.BTC_EUR)

        Assert.assertEquals(1, btcEurSources.size)
        Assert.assertEquals(coinbase, btcEurSources.first())

        verify(bitstamp).getCurrencyPairs()
        verify(coinbase).getCurrencyPairs()

        val btcUsdSources = repository.getExchangeRatesSources(CurrencyPair.BTC_USD)
        Assert.assertTrue(btcUsdSources.isEmpty())

        val btcGbpSources = repository.getExchangeRatesSources(CurrencyPair.BTC_GBP)
        Assert.assertEquals(1, btcGbpSources.size)
        Assert.assertEquals(coinbase, btcGbpSources.first())
    }
}