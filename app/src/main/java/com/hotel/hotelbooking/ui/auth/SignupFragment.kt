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
import com.hotel.hotelbooking.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRoleChips()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRoleChips() {
        binding.chipGroup.setOnCheckedStateChangeListener { group, _ ->
            viewModel.selectedRole = when (group.checkedChipId) {
                R.id.chip_owner -> UserRole.ADMIN
                else -> UserRole.CUSTOMER
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            viewModel.submit(
                fullName = binding.etName.text?.toString().orEmpty(),
                email = binding.etEmail.text?.toString().orEmpty(),
                phone = binding.etPhone.text?.toString().orEmpty(),
                password = binding.etPassword.text?.toString().orEmpty()
            )
        }
        binding.btnGoLogin.setOnClickListener { findNavController().popBackStack() }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.btnSignUp.isEnabled = !state.loading

            binding.tilName.error = state.nameError
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
                val action = if (role == UserRole.ADMIN) R.id.action_signup_to_admin else R.id.action_signup_to_customer
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
