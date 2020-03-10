package com.bubelov.coins.rates

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.rate.ExchangeRatesSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat

class ExchangeRatesViewModel(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    val selectedPair = flow {
        while (true) {
            emit(_selectedPair)
            delay(100)
        }
    }

    private var _selectedPair = CurrencyPair.BTC_USD

    val rows = flow {
        while (true) {
            emit(_rows)
            delay(100)
        }
    }

    private var _rows = listOf<ExchangeRateRow>()

    fun setSelectedPair(pair: CurrencyPair) {
        _selectedPair = pair
        refreshRates()
    }

    fun refreshRates() {
        viewModelScope.launch {
            val sources = exchangeRatesRepository.getExchangeRatesSources(_selectedPair)

            val rows = sources
                .map { it.toRow("Loading") }
                .toMutableList()

            this@ExchangeRatesViewModel._rows = rows

            val results = mutableListOf<Deferred<Unit>>()

            sources.forEachIndexed { index, source ->
                results += async {
                    val rate = withContext(Dispatchers.IO) {
                        source.getExchangeRate(_selectedPair)
                    }

                    val row = when (rate) {
                        is Result.Success -> {
                            source.toRow(RATE_FORMAT.format(rate.data))
                        }

                        is Result.Error -> {
                            source.toRow("Error")
                        }
                    }

                    if (isActive) {
                        rows[index] = row
                        this@ExchangeRatesViewModel._rows = rows
                    }
                }
            }

            results.forEach { it.await() }
        }
    }

    private fun ExchangeRatesSource.toRow(value: String): ExchangeRateRow {
        return ExchangeRateRow(name[0].toString(), name, value)
    }

    companion object {
        private val RATE_FORMAT = NumberFormat.getNumberInstance().apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    }
}