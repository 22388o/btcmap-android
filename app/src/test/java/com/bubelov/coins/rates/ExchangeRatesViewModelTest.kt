package com.bubelov.coins.rates

import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.rate.ExchangeRatesSource
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ExchangeRatesViewModelTest {

    @Mock
    lateinit var exchangeRatesRepository: ExchangeRatesRepository

    lateinit var model: ExchangeRatesViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        model = ExchangeRatesViewModel(exchangeRatesRepository)
    }

    @Test
    fun setSelectedPair() = runBlocking {
        Dispatchers.setMain(Dispatchers.Default)
        val pair = CurrencyPair.BTC_EUR
        model.setSelectedPair(pair)
        assert(model.selectedPair.take(1).first() == pair)
    }

    @Test
    fun refreshRates() = runBlocking {
        Dispatchers.setMain(Dispatchers.Default)

        val pair = CurrencyPair.BTC_EUR

        whenever(exchangeRatesRepository.getExchangeRatesSources(pair)).thenReturn(listOf(
            object : ExchangeRatesSource {
                override val name = "Test"

                override fun getCurrencyPairs() = listOf(CurrencyPair.BTC_EUR)

                override suspend fun getExchangeRate(pair: CurrencyPair) = Result.Success(5.0)
            }
        ))

        model.setSelectedPair(pair)
        model.refreshRates()

        val rows = model.rows.take(2).toList().last()
        assert(rows.size == 1)
        assert(rows.first().value == "5.00")
    }
}