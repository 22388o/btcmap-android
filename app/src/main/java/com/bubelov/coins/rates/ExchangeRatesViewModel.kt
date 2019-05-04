package com.bubelov.coins.rates

import androidx.lifecycle.ViewModel
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.rate.ExchangeRatesSource
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.text.NumberFormat
import javax.inject.Inject

class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {
    private val rootJob = Job()
    private var fetchRatesJob: Job? = null
    private val uiScope = CoroutineScope(Dispatchers.Main + rootJob)

    private lateinit var selectedPairCollector: FlowCollector<CurrencyPair>
    val selectedPair = flow<CurrencyPair> { selectedPairCollector = this }

    private lateinit var rowsCollector: FlowCollector<List<ExchangeRateRow>>
    val rows = flow<List<ExchangeRateRow>> { rowsCollector = this }

    override fun onCleared() {
        super.onCleared()
        rootJob.cancel()
    }

    fun selectCurrencyPair(pair: CurrencyPair) {
        uiScope.launch {
            selectedPairCollector.emit(pair)
        }

        fetchRatesJob?.cancel()

        fetchRatesJob = uiScope.launch {
            val sources = exchangeRatesRepository.getExchangeRatesSources(pair)

            val rows = sources
                .map { it.toRow("Loading") }
                .toMutableList()

            rowsCollector.emit(rows)

            val results = mutableListOf<Deferred<Unit>>()

            sources.forEachIndexed { index, source ->
                results += async {
                    val rate = withContext(Dispatchers.IO) {
                        source.getExchangeRate(pair)
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
                        rowsCollector.emit(rows)
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