package com.example.project.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.project.R
import com.example.project.models.Name

class CategoryAdapter(
    private val onItemClick: (Int) -> Unit
) : ListAdapter<Name, CategoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Name>() {
            override fun areItemsTheSame(oldItem: Name, newItem: Name): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Name, newItem: Name): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_name_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvName)
        private val tvMeaning: TextView = view.findViewById(R.id.tvMeaning)

        fun bind(item: Name) {
            tvName.text = item.name
            tvMeaning.text = item.meaningEng

            itemView.setOnClickListener {
                onItemClick(item.id)
            }
        }
    }
}
