package com.hotel.hotelbooking.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.databinding.ItemStatPropertyCardBinding
import com.hotel.hotelbooking.ui.util.loadImage

class StatPropertyAdapter : ListAdapter<Property, StatPropertyAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemStatPropertyCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            binding.tvName.text = property.name
            binding.tvAddress.text = property.address
            binding.tvRating.text = String.format("%.1f", property.rating)
            binding.ivProperty.loadImage(property.imageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStatPropertyCardBinding.inflate(
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
