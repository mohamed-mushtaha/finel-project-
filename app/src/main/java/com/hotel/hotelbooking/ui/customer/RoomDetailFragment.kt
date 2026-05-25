package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentRoomDetailBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomMargin
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomDetailFragment : Fragment() {

    private var _binding: FragmentRoomDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.fabBook.applySystemBarBottomMargin(
            resources.getDimensionPixelOffset(R.dimen.spacing_md)
        )

        binding.fabBook.setOnClickListener {
            val args = RoomDetailFragmentArgs.fromBundle(requireArguments())
            findNavController().navigate(
                RoomDetailFragmentDirections.actionRoomDetailToBookingConfirm(
                    roomId = args.roomId,
                    propertyId = args.propertyId
                )
            )
        }

        viewModel.room.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progress.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    resource.data?.let { room ->
                        binding.toolbar.title = room.name
                        binding.tvRoomName.text = room.name
                        binding.tvPrice.text = getString(R.string.price_value, room.pricePerNight)
                        binding.tvRating.text = String.format("%.1f", room.rating)
                        binding.tvCapacity.text = resources.getQuantityString(
                            R.plurals.guests_count, room.capacity, room.capacity
                        )
                        binding.tvDescription.text = room.description
                        binding.ivRoom.loadImage(room.imageUrl)

                        val available = room.available
                        binding.chipAvailable.text = getString(
                            if (available) R.string.room_available else R.string.room_unavailable
                        )
                        val chipColor = if (available) R.color.emerald_100 else R.color.coral_100
                        val textColor = if (available) R.color.emerald_800 else R.color.coral_700
                        binding.chipAvailable.chipBackgroundColor =
                            ContextCompat.getColorStateList(requireContext(), chipColor)
                        binding.chipAvailable.setTextColor(
                            ContextCompat.getColor(requireContext(), textColor)
                        )

                        binding.fabBook.isEnabled = available

                        if (room.amenities.isNotEmpty()) {
                            binding.tvAmenitiesLabel.visibility = View.VISIBLE
                            binding.chipGroupAmenities.removeAllViews()
                            room.amenities.forEach { amenity ->
                                val chip = Chip(requireContext()).apply {
                                    text = amenity
                                    isClickable = false
                                    chipBackgroundColor = ContextCompat.getColorStateList(
                                        requireContext(), R.color.emerald_50
                                    )
                                }
                                binding.chipGroupAmenities.addView(chip)
                            }
                        }
                    }
                }
                is Resource.Error -> binding.progress.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
