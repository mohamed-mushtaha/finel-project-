package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentBookingsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookingsFragment : Fragment() {

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingsViewModel by viewModels()
    private val adapter = BookingAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvBookings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBookings.adapter = adapter

        viewModel.bookings.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    adapter.submitList(resource.data)
                    binding.layoutEmpty.visibility =
                        if (resource.data.isEmpty()) View.VISIBLE else View.GONE
                    binding.rvBookings.visibility =
                        if (resource.data.isEmpty()) View.GONE else View.VISIBLE
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
