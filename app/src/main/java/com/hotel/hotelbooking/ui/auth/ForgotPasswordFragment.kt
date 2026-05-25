package com.hotel.hotelbooking.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.hotel.hotelbooking.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener { findNavController().popBackStack() }
        binding.btnSend.setOnClickListener {
            viewModel.submit(binding.etEmail.text?.toString().orEmpty())
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            binding.btnSend.isEnabled = !state.loading
            binding.tilEmail.error = state.emailError

            if (state.formError != null) {
                binding.tvFormError.isVisible = true
                binding.tvFormError.text = state.formError
            } else {
                binding.tvFormError.isVisible = false
            }

            if (state.sentToEmail != null) {
                binding.layoutInput.isVisible = false
                binding.layoutSuccess.isVisible = true
                binding.tvSuccessMessage.text =
                    getString(com.hotel.hotelbooking.R.string.reset_sent_message, state.sentToEmail)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
