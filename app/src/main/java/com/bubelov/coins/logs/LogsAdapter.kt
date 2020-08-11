package com.bubelov.coins.logs

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bubelov.coins.R
import com.bubelov.coins.data.LogEntry
import kotlinx.android.synthetic.main.row_log_entry.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LogsAdapter : RecyclerView.Adapter<LogsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        val DATE_TIME_FORMATTER: DateTimeFormatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    private val items = mutableListOf<LogEntry>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_log_entry,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.apply {
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