package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.databinding.FragmentBookingConfirmBinding
import com.hotel.hotelbooking.ui.auth.AuthGateViewModel
import com.hotel.hotelbooking.ui.util.applySystemBarBottomPadding
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class BookingConfirmFragment : Fragment() {

    private var _binding: FragmentBookingConfirmBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingConfirmViewModel by viewModels()
    private val authGateViewModel: AuthGateViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.bottomBar.applySystemBarBottomPadding()

        authGateViewModel.user.value?.let { viewModel.setUserId(it.uid) }

        binding.btnPickDates.setOnClickListener { showDatePicker() }
        binding.tvCheckIn.setOnClickListener { showDatePicker() }
        binding.tvCheckOut.setOnClickListener { showDatePicker() }

        binding.btnGuestsPlus.setOnClickListener { viewModel.incrementGuests() }
        binding.btnGuestsMinus.setOnClickListener { viewModel.decrementGuests() }

        binding.btnConfirm.setOnClickListener { viewModel.confirmBooking() }

        viewModel.state.observe(viewLifecycleOwner) { s ->
            // Room info
            s.room?.let { room ->
                binding.tvRoomName.text = room.name
                binding.ivRoom.loadImage(room.imageUrl)
                val priceStr = getString(R.string.price_per_night, room.pricePerNight)
                binding.tvPricePerNight.text = priceStr
            }
            s.property?.let { binding.tvPropertyName.text = it.name }

            // Dates
            val fmt = SimpleDateFormat("EEE, MMM d yyyy", Locale.getDefault())
            binding.tvCheckIn.text = if (s.checkIn != null) fmt.format(Date(s.checkIn))
                else getString(R.string.tap_to_select)
            binding.tvCheckOut.text = if (s.checkOut != null) fmt.format(Date(s.checkOut))
                else getString(R.string.tap_to_select)

            // Guests
            binding.tvGuests.text = s.guests.toString()

            // Price summary
            if (s.nights > 0 && s.room != null) {
                val nightsLabel = getString(
                    R.string.price_nights_label,
                    getString(R.string.price_value, s.room.pricePerNight),
                    s.nights
                )
                binding.tvNightsLabel.text = nightsLabel
                binding.tvNightsTotal.text = getString(R.string.price_value, s.totalPrice)
                binding.tvTotal.text = getString(R.string.price_value, s.totalPrice)
            }

            // Error
            if (s.error != null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = s.error
            } else {
                binding.tvError.visibility = View.GONE
            }

            // Progress / button
            binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
            binding.btnConfirm.isEnabled = s.canConfirm

            // Success
            if (s.bookingSuccess) {
                Snackbar.make(binding.root, getString(R.string.booking_success), Snackbar.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun showDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())
            .build()
        val picker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(getString(R.string.select_dates))
            .setCalendarConstraints(constraints)
            .build()

        picker.addOnPositiveButtonClickListener { selection ->
            val checkIn = selection.first ?: return@addOnPositiveButtonClickListener
            val checkOut = selection.second ?: return@addOnPositiveButtonClickListener
            viewModel.setDates(checkIn, checkOut)
        }
        picker.show(childFragmentManager, "date_range_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
