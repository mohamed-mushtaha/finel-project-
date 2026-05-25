package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentPropertyDetailBinding
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyDetailFragment : Fragment() {

    private var _binding: FragmentPropertyDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyDetailViewModel by viewModels()
    private val roomAdapter = RoomAdapter { room ->
        findNavController().navigate(
            PropertyDetailFragmentDirections.actionPropertyDetailToRoomDetail(
                roomId = room.id,
                propertyId = room.propertyId
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.rvRooms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRooms.adapter = roomAdapter

        binding.btnViewMap.setOnClickListener {
            val propertyId = PropertyDetailFragmentArgs.fromBundle(requireArguments()).propertyId
            findNavController().navigate(
                PropertyDetailFragmentDirections.actionPropertyDetailToMap(propertyId)
            )
        }

        viewModel.property.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> binding.progress.visibility = View.VISIBLE
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    resource.data?.let { property ->
                        binding.collapsingToolbar.title = property.name
                        binding.tvPropertyName.text = property.name
                        binding.tvRating.text = getString(
                            com.hotel.hotelbooking.R.string.rating_display,
                            property.rating
                        )
                        binding.tvAddress.text = property.address
                        binding.ivProperty.loadImage(property.imageUrl)
                        binding.chipType.text = when (property.type) {
                            PropertyType.HOTEL -> getString(com.hotel.hotelbooking.R.string.type_hotel)
                            PropertyType.APARTMENT -> getString(com.hotel.hotelbooking.R.string.type_apartment)
                        }
                        if (property.description.isNotBlank()) {
                            binding.tvDescription.visibility = View.VISIBLE
                            binding.tvDescription.text = property.description
                        }
                    }
                }
                is Resource.Error -> binding.progress.visibility = View.GONE
            }
        }

        viewModel.rooms.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    roomAdapter.submitList(resource.data)
                    binding.tvNoRooms.visibility =
                        if (resource.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> binding.tvNoRooms.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
