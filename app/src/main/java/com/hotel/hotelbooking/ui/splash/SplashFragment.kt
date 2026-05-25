package com.hotel.hotelbooking.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.UserRole
import com.hotel.hotelbooking.databinding.FragmentSplashBinding
import com.hotel.hotelbooking.ui.auth.AuthGateViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val authGateViewModel: AuthGateViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authGateViewModel.ready.observe(viewLifecycleOwner) { ready ->
            if (!ready) return@observe
            viewLifecycleOwner.lifecycleScope.launch {
                delay(700)
                val user = authGateViewModel.user.value
                val action = when {
                    user == null -> R.id.action_splash_to_login
                    user.role == UserRole.ADMIN -> R.id.action_splash_to_admin
                    else -> R.id.action_splash_to_customer
                }
                if (isAdded) findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
