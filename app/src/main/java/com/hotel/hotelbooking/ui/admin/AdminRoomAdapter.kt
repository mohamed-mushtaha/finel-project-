package com.hotel.hotelbooking.ui.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.databinding.ItemRoomAdminCardBinding
import com.hotel.hotelbooking.ui.util.loadImage

class AdminRoomAdapter(
    private val onEdit: (Room) -> Unit,
    private val onDelete: (Room) -> Unit
) : ListAdapter<Room, AdminRoomAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemRoomAdminCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            binding.tvRoomName.text = room.name
            binding.tvPrice.text = binding.root.context.getString(
                R.string.price_per_night, room.pricePerNight
            )
            binding.ivRoom.loadImage(room.imageUrl)
            binding.btnEditRoom.setOnClickListener { onEdit(room) }
            binding.btnDeleteRoom.setOnClickListener { onDelete(room) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoomAdminCardBinding.inflate(
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
