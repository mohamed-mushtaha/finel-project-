package com.hotel.hotelbooking.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.UserRole
import com.hotel.hotelbooking.databinding.FragmentProfileBinding
import com.hotel.hotelbooking.ui.auth.AuthGateViewModel
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private val authGateViewModel: AuthGateViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.btnEdit.setOnClickListener {
            findNavController().navigate(R.id.action_profile_to_edit_profile)
        }

        binding.btnSignOut.setOnClickListener {
            authGateViewModel.signOut()
            // Sign-out must always reach the root login, regardless of which nav graph
            // hosts this fragment (main graph or nested customer graph).
            val rootNavController = (requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment)
                .navController
            rootNavController.navigate(
                R.id.loginFragment,
                null,
                NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            val user = state.user ?: return@observe
            binding.ivAvatar.loadImage(user.photoUrl.ifBlank { null })
            binding.tvName.text = user.fullName.ifBlank { user.email }
            binding.tvEmail.text = user.email
            binding.tvPhone.text = user.phone.ifBlank { "—" }
            binding.chipRole.text = if (user.role == UserRole.ADMIN)
                getString(R.string.role_display_admin)
            else
                getString(R.string.role_display_customer)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
