package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.hotelbooking.databinding.FragmentAdminDashboardBinding
import com.hotel.hotelbooking.ui.customer.RoomAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AdminDashboardViewModel by viewModels()
    private val topRatedAdapter = StatPropertyAdapter()
    private val mostRequestedAdapter = RoomAdapter { /* no-op in dashboard */ }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvTopRated.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTopRated.adapter = topRatedAdapter
        binding.rvTopRated.isNestedScrollingEnabled = false

        binding.rvMostBooked.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMostBooked.adapter = mostRequestedAdapter
        binding.rvMostBooked.isNestedScrollingEnabled = false

        viewModel.state.observe(viewLifecycleOwner) { s ->
            binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
            binding.tvTotalProperties.text = s.totalProperties.toString()
            binding.tvTotalBookings.text = s.totalBookings.toString()
            topRatedAdapter.submitList(s.topRatedProperties)
            mostRequestedAdapter.submitList(s.mostRequestedRooms)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
