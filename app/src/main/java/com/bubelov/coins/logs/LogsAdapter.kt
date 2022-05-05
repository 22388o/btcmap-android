package com.bubelov.coins.logs

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bubelov.coins.data.LogEntry
import com.bubelov.coins.databinding.RowLogEntryBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogsAdapter : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

    class ViewHolder(val binding: RowLogEntryBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    private val items = mutableListOf<LogEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RowLogEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            meta.text = buildString {
                append(LocalDateTime.parse(item.datetime).format(DATE_TIME_FORMATTER))

                if (item.tag.isNotBlank()) {
                    append(" · ${item.tag}")
                }
            }

            message.text = item.message
        }
    }

    override fun getItemCount() = items.size

    fun swapItems(newItems: Collection<LogEntry>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}