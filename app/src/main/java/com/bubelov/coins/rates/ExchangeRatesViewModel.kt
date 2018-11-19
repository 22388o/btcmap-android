/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.rates

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.rate.ExchangeRatesSource
import kotlinx.coroutines.*
import java.text.NumberFormat
import javax.inject.Inject

class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {
    private val mainJob = Job()
    private var fetchRatesJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + mainJob)

    private val _currencyPair = MutableLiveData<CurrencyPair>()
    val currencyPair: LiveData<CurrencyPair> = _currencyPair

    private val _ratesRows = MutableLiveData<List<ExchangeRateRow>>()
    val ratesRows: LiveData<List<ExchangeRateRow>> = _ratesRows

    override fun onCleared() {
        super.onCleared()
        mainJob.cancel()
    }

    fun selectCurrencyPair(pair: CurrencyPair) {
        _currencyPair.value = pair

        fetchRatesJob.cancel()

        fetchRatesJob = uiScope.launch {
            val sources = exchangeRatesRepository.getExchangeRatesSources(pair)

            val rows = sources
                .map { it.toRow("Loading") }
                .toMutableList()

            _ratesRows.value = rows

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
                        _ratesRows.value = rows
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