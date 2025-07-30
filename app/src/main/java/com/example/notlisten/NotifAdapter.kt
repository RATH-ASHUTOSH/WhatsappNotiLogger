package com.example.notlisten

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotifAdapter(
    private val items: MutableList<String>,
    private val onItemRemoved: (Int) -> Unit
) : RecyclerView.Adapter<NotifAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textItem: TextView = itemView.findViewById(R.id.textItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_notif_adapter, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textItem.text = items[position]
    }

    override fun getItemCount(): Int = items.size

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        onItemRemoved(position)
    }

    fun filterList(filtered: List<String>) {
        items.clear()
        items.addAll(filtered)
        notifyDataSetChanged()
    }
}
