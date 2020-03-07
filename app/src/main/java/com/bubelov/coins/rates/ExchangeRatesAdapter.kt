package com.bubelov.coins.rates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bubelov.coins.R
import kotlinx.android.synthetic.main.row_exchange_rate.view.*

class ExchangeRatesAdapter(private val items: List<ExchangeRateRow>) :
    RecyclerView.Adapter<ExchangeRatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_exchange_rate,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ExchangeRateRow) {
            itemView.apply {
                icon_text.text = item.iconText
                title.text = item.title
                value.text = item.value
            }
        }
    }
}