package com.hotel.hotelbooking.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.databinding.ItemPropertyCardBinding
import com.hotel.hotelbooking.ui.util.loadImage

class PropertyAdapter(
    private val onItemClick: (Property) -> Unit
) : ListAdapter<Property, PropertyAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemPropertyCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            binding.tvPropertyName.text = property.name
            binding.tvRating.text = String.format("%.1f", property.rating)
            binding.tvAddress.text = property.address
            binding.chipType.text = property.type.name.lowercase().replaceFirstChar { it.uppercase() }
            binding.ivProperty.loadImage(property.imageUrl)
            binding.root.setOnClickListener { onItemClick(property) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPropertyCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Property>() {
        override fun areItemsTheSame(old: Property, new: Property) = old.id == new.id
        override fun areContentsTheSame(old: Property, new: Property) = old == new
    }
}
