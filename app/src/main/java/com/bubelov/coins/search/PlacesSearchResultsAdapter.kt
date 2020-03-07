package com.bubelov.coins.search

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.bubelov.coins.R

import kotlinx.android.synthetic.main.row_places_search_result.view.*

class PlacesSearchResultsAdapter(
    private val itemClick: (PlacesSearchRow) -> Unit
) : RecyclerView.Adapter<PlacesSearchResultsAdapter.ViewHolder>() {

    private val items = mutableListOf<PlacesSearchRow>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_places_search_result,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.itemView.apply {
            icon.setImageBitmap(item.icon)
            name.text = item.name
            distance.visibility = if (item.distance.isNotEmpty()) View.VISIBLE else View.GONE
            distance.text = item.distance
            setOnClickListener { itemClick(item) }
        }
    }

    override fun getItemCount() = items.size

    fun swapItems(newItems: Collection<PlacesSearchRow>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view)
}