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

import android.arch.lifecycle.*
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.rate.ExchangeRatesRepository
import com.bubelov.coins.repository.rate.ExchangeRatesSource
import kotlinx.coroutines.*
import kotlinx.coroutines.android.Main
import java.text.NumberFormat
import javax.inject.Inject

class ExchangeRatesViewModel @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {
    private val job = Job()
    private val uiScope = CoroutineScope(kotlinx.coroutines.Dispatchers.Main + job)

    private val _currencyPair = MutableLiveData<CurrencyPair>()
    val currencyPair: LiveData<CurrencyPair> = _currencyPair

    private val _ratesRows = MutableLiveData<List<ExchangeRateRow>>()
    val ratesRows: LiveData<List<ExchangeRateRow>> = _ratesRows

    fun selectCurrencyPair(pair: CurrencyPair) {
        _currencyPair.value = pair

        uiScope.launch {
            val sources = exchangeRatesRepository.getExchangeRatesSources(pair)

            val rows = sources
                .map { it.toRow("Loading") }
                .toMutableList()

            _ratesRows.value = rows

            sources.forEachIndexed { index, source ->
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

                rows[index] = row
                _ratesRows.value = rows
            }
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