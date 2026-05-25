package com.hotel.hotelbooking.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.UserRole
import com.hotel.hotelbooking.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            viewModel.submit(
                email = binding.etEmail.text?.toString().orEmpty(),
                password = binding.etPassword.text?.toString().orEmpty()
            )
        }
        binding.btnGoSignup.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_signup)
        }
        binding.btnForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_forgot)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.btnSignIn.isEnabled = !state.loading

            binding.tilEmail.error = state.emailError
            binding.tilPassword.error = state.passwordError

            if (state.formError != null) {
                binding.tvFormError.isVisible = true
                binding.tvFormError.text = state.formError
            } else {
                binding.tvFormError.isVisible = false
            }

            state.navigateTo?.let { role ->
                viewModel.clearNavigation()
                val action = if (role == UserRole.ADMIN) R.id.action_login_to_admin else R.id.action_login_to_customer
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
