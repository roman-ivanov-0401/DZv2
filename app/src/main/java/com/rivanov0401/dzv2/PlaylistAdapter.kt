package com.rivanov0401.dzv2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(
    private val playlists: List<Pair<Int, String>>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val (id, name) = playlists[position]
        holder.idTextView.text = "ID: $id"
        holder.nameTextView.text = "Name: $name"

        holder.itemView.setOnClickListener {
            onItemClick(id)
        }
    }

    override fun getItemCount(): Int = playlists.size

    class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idTextView: TextView = view.findViewById(android.R.id.text1)
        val nameTextView: TextView = view.findViewById(android.R.id.text2)
    }
}
