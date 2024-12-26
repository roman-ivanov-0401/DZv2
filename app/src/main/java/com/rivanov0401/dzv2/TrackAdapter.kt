package com.rivanov0401.dzv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(
    private val tracks: List<Pair<Int, String>>,
    private val onClick: (Int, String) -> Unit
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val (id, name) = tracks[position]
        holder.idTextView.text = "ID: $id"
        holder.nameTextView.text = "Name: $name"
        holder.itemView.setOnClickListener { onClick(id, name) }
    }

    override fun getItemCount(): Int = tracks.size

    class TrackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idTextView: TextView = view.findViewById(android.R.id.text1)
        val nameTextView: TextView = view.findViewById(android.R.id.text2)
    }
}
