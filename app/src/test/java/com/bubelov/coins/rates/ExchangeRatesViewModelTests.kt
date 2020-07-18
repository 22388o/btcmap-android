package com.bubelov.coins.rates

import com.bubelov.coins.TestSuite
import com.bubelov.coins.model.CurrencyPair
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.koin.core.inject

class ExchangeRatesViewModelTests : TestSuite() {

    val model: ExchangeRatesViewModel by inject()

    @Test
    fun setSelectedPair() = runBlocking {
        val pair = CurrencyPair.BTC_EUR
        model.setSelectedPair(pair)
        assert(model.selectedPair.take(1).first() == pair)
    }

//    @Test
//    fun refreshRates() = runBlocking {
//        val pair = CurrencyPair.BTC_EUR
//
//        declareMock<ExchangeRatesRepository> {
//            given(getExchangeRatesSources(pair)).willReturn(listOf(
//                object : ExchangeRatesSource {
//                    override val name = "Test"
//
//                    override fun getCurrencyPairs() = listOf(CurrencyPair.BTC_EUR)
//
//                    override suspend fun getExchangeRate(pair: CurrencyPair) = Result.Success(5.0)
//                }
//            ))
//        }
//
//        model.setSelectedPair(pair)
//        model.refreshRates()
//
//        val rows = model.rows.take(2).toList().last()
//        assert(rows.size == 1)
//        assert(rows.first().value == "5.00")
//    }
}