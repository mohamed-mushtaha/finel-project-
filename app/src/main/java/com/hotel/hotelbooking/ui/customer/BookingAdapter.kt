package com.hotel.hotelbooking.ui.customer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.Booking
import com.hotel.hotelbooking.data.model.BookingStatus
import com.hotel.hotelbooking.databinding.ItemBookingCardBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class BookingAdapter : ListAdapter<Booking, BookingAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemBookingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: Booking) {
            binding.tvPropertyName.text = booking.propertyName
            binding.tvRoomName.text = booking.roomName
            binding.tvTotal.text = binding.root.context.getString(
                R.string.price_value, booking.totalPrice
            )

            val fmt = SimpleDateFormat("MMM d", Locale.getDefault())
            val yearFmt = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            val nights = TimeUnit.MILLISECONDS.toDays(booking.checkOut - booking.checkIn).toInt()
            val checkInDate = Date(booking.checkIn)
            val checkOutDate = Date(booking.checkOut)
            binding.tvDates.text = binding.root.context.getString(
                R.string.booking_dates_nights,
                fmt.format(checkInDate),
                yearFmt.format(checkOutDate),
                nights
            )

            val (chipText, chipBg, chipText2) = when (booking.status) {
                BookingStatus.CONFIRMED -> Triple(
                    R.string.status_confirmed, R.color.emerald_100, R.color.emerald_800
                )
                BookingStatus.PENDING -> Triple(
                    R.string.status_pending, R.color.sand_200, R.color.slate_700
                )
                BookingStatus.CANCELLED -> Triple(
                    R.string.status_cancelled, R.color.coral_100, R.color.coral_700
                )
                BookingStatus.COMPLETED -> Triple(
                    R.string.status_completed, R.color.emerald_50, R.color.emerald_700
                )
            }
            binding.chipStatus.text = binding.root.context.getString(chipText)
            binding.chipStatus.chipBackgroundColor =
                ContextCompat.getColorStateList(binding.root.context, chipBg)
            binding.chipStatus.setTextColor(
                ContextCompat.getColor(binding.root.context, chipText2)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBookingCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private companion object DiffCallback : DiffUtil.ItemCallback<Booking>() {
        override fun areItemsTheSame(old: Booking, new: Booking) = old.id == new.id
        override fun areContentsTheSame(old: Booking, new: Booking) = old == new
    }
}
