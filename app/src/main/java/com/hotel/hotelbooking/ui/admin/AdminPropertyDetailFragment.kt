package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentAdminPropertyDetailBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomMargin
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPropertyDetailFragment : Fragment() {

    private var _binding: FragmentAdminPropertyDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminPropertyDetailViewModel by viewModels()

    private val roomAdapter = AdminRoomAdapter(
        onEdit = { room ->
            findNavController().navigate(
                AdminPropertyDetailFragmentDirections.actionAdminPropertyDetailToEditRoom(
                    propertyId = viewModel.propertyId,
                    roomId = room.id
                )
            )
        },
        onDelete = { room ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_room)
                .setMessage(getString(R.string.delete_room_confirm, room.name))
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteRoom(room.id) }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPropertyDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.fabAddRoom.applySystemBarBottomMargin(
            resources.getDimensionPixelOffset(R.dimen.spacing_md)
        )

        binding.rvRooms.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRooms.adapter = roomAdapter

        binding.fabAddRoom.setOnClickListener {
            findNavController().navigate(
                AdminPropertyDetailFragmentDirections.actionAdminPropertyDetailToAddRoom(
                    propertyId = viewModel.propertyId,
                    roomId = ""
                )
            )
        }

        binding.btnEditProperty.setOnClickListener {
            findNavController().navigate(
                AdminPropertyDetailFragmentDirections.actionAdminPropertyDetailToEditProperty(
                    propertyId = viewModel.propertyId
                )
            )
        }

        binding.btnDeleteProperty.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_property)
                .setMessage(R.string.delete_property_confirm)
                .setPositiveButton(R.string.delete) { _, _ -> viewModel.deleteProperty() }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }

        viewModel.property.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> Unit
                is Resource.Success -> {
                    resource.data?.let { p ->
                        binding.toolbar.title = p.name
                        binding.tvPropertyName.text = p.name
                        binding.tvRating.text = getString(R.string.rating_display, p.rating)
                        binding.tvAddress.text = p.address
                        binding.ivProperty.loadImage(p.imageUrl)
                        binding.chipType.text = when (p.type) {
                            PropertyType.HOTEL -> getString(R.string.type_hotel)
                            PropertyType.APARTMENT -> getString(R.string.type_apartment)
                        }
                    }
                }
                is Resource.Error -> Unit
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

        viewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result ?: return@observe
            viewModel.consumeDeleteResult()
            result.onSuccess { findNavController().popBackStack() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
