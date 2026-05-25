package com.hotel.hotelbooking.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.databinding.ItemRoomCardBinding
import com.hotel.hotelbooking.ui.util.loadImage

class RoomAdapter(
    private val onItemClick: (Room) -> Unit
) : ListAdapter<Room, RoomAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemRoomCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            binding.tvRoomName.text = room.name
            binding.tvRoomRating.text = String.format("%.1f", room.rating)
            binding.tvCapacity.text = binding.root.context.resources
                .getQuantityString(R.plurals.guests_count, room.capacity, room.capacity)
            binding.tvPrice.text = binding.root.context.getString(R.string.price_per_night, room.pricePerNight)
            binding.ivRoom.loadImage(room.imageUrl)

            val available = room.available
            binding.chipAvailable.text = binding.root.context.getString(
                if (available) R.string.room_available else R.string.room_unavailable
            )
            val chipColor = if (available) R.color.emerald_100 else R.color.coral_100
            val textColor = if (available) R.color.emerald_800 else R.color.coral_700
            binding.chipAvailable.chipBackgroundColor = ContextCompat.getColorStateList(
                binding.root.context, chipColor
            )
            binding.chipAvailable.setTextColor(
                ContextCompat.getColor(binding.root.context, textColor)
            )

            binding.root.setOnClickListener { onItemClick(room) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoomCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(old: Room, new: Room) = old.id == new.id
        override fun areContentsTheSame(old: Room, new: Room) = old == new
    }
}
