package rates

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bubelov.coins.databinding.RowExchangeRateBinding

class ExchangeRatesAdapter(private val items: List<ExchangeRateRow>) :
    RecyclerView.Adapter<ExchangeRatesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowExchangeRateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false,
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount() = items.size

    class ViewHolder(val binding: RowExchangeRateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ExchangeRateRow) {
            binding.apply {
                iconText.text = item.iconText
                title.text = item.title
                value.text = item.value
            }
        }
    }
}