package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.databinding.FragmentAdminHomeBinding
import com.hotel.hotelbooking.ui.auth.AuthGateViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminHomeFragment : Fragment() {

    private var _binding: FragmentAdminHomeBinding? = null
    private val binding get() = _binding!!
    private val authGateViewModel: AuthGateViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authGateViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.tvGreeting.text = "Admin panel — ${user.fullName.ifBlank { "Admin" }}"
            }
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_profile -> {
                    findNavController().navigate(R.id.action_admin_to_profile)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
