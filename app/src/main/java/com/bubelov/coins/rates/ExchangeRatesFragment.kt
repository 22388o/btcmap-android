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

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bubelov.coins.R
import com.bubelov.coins.model.CurrencyPair
import com.bubelov.coins.util.viewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_exchange_rates.*
import javax.inject.Inject

class ExchangeRatesFragment : DaggerFragment() {
    @Inject lateinit var modelFactory: ViewModelProvider.Factory
    private val model by lazy { viewModelProvider(modelFactory) as ExchangeRatesViewModel }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exchange_rates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        toolbar.apply {
            setNavigationOnClickListener { findNavController().popBackStack() }

            inflateMenu(R.menu.exchange_rates)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.currency -> {
                        val items = CurrencyPair.values()
                        val itemTitles = items.map { it.toString() }.toTypedArray()

                        AlertDialog.Builder(requireContext())
                            .setTitle(R.string.currency)
                            .setItems(itemTitles) { _, index ->
                                model.selectCurrencyPair(items[index])
                            }
                            .show()
                    }
                }

                true
            }
        }

        ratesView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())

        model.currencyPair.observe(this, Observer { pair ->
            toolbar.menu.findItem(R.id.currency).title = pair.toString()
        })

        model.ratesRows.observe(this, Observer { rows ->
            ratesView.adapter = ExchangeRatesAdapter(rows ?: emptyList())
        })

        model.selectCurrencyPair(CurrencyPair.BTC_USD)
    }
}