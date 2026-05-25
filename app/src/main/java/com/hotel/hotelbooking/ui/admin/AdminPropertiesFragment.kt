package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentAdminPropertiesBinding
import com.hotel.hotelbooking.ui.customer.PropertyAdapter
import com.hotel.hotelbooking.ui.util.applySystemBarBottomMargin
import com.hotel.hotelbooking.ui.util.attachFabScrollBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminPropertiesFragment : Fragment() {

    private var _binding: FragmentAdminPropertiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminPropertiesViewModel by viewModels()
    private val adapter = PropertyAdapter { property ->
        findNavController().navigate(
            AdminPropertiesFragmentDirections
                .actionAdminPropertiesToAdminPropertyDetail(property.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminPropertiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProperties.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProperties.adapter = adapter
        binding.rvProperties.attachFabScrollBehavior(binding.fabAdd)

        // FAB must clear the BottomNav (64 dp) plus a comfortable gap (16 dp)
        val fabBase = resources.getDimensionPixelOffset(R.dimen.bottom_nav_height) +
            resources.getDimensionPixelOffset(R.dimen.spacing_md)
        binding.fabAdd.applySystemBarBottomMargin(fabBase)

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(
                AdminPropertiesFragmentDirections.actionAdminPropertiesToAddProperty(propertyId = "")
            )
        }

        viewModel.properties.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    adapter.submitList(resource.data)
                    val empty = resource.data.isEmpty()
                    binding.layoutEmpty.visibility = if (empty) View.VISIBLE else View.GONE
                    binding.rvProperties.visibility = if (empty) View.GONE else View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
