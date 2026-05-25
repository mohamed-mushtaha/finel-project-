package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.color.MaterialColors
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentExploreBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExploreViewModel by viewModels()
    private val adapter = PropertyAdapter { property ->
        findNavController().navigate(
            ExploreFragmentDirections.actionExploreToPropertyDetail(property.id)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvProperties.layoutManager = LinearLayoutManager(requireContext())
        binding.rvProperties.adapter = adapter

        binding.swipeRefresh.setColorSchemeColors(
            MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorPrimary)
        )
        binding.swipeRefresh.setOnRefreshListener { viewModel.refresh() }

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val type = when (checkedIds.firstOrNull()) {
                R.id.chip_hotels -> PropertyType.HOTEL
                R.id.chip_apartments -> PropertyType.APARTMENT
                else -> null
            }
            viewModel.setFilter(type)
        }

        viewModel.properties.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    if (!binding.swipeRefresh.isRefreshing) {
                        binding.progress.visibility = View.VISIBLE
                    }
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(resource.data)
                    binding.layoutEmpty.visibility =
                        if (resource.data.isEmpty()) View.VISIBLE else View.GONE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
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
