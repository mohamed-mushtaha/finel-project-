package com.hotel.hotelbooking.ui.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.databinding.FragmentEditProfileBinding
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivAvatar.loadImage(uri.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

        binding.fabPickImage.setOnClickListener { pickImage.launch("image/*") }

        binding.btnSave.setOnClickListener {
            viewModel.saveProfile(
                fullName = binding.etName.text?.toString().orEmpty(),
                phone = binding.etPhone.text?.toString().orEmpty(),
                avatarUri = selectedImageUri
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            // Pre-fill fields on first load
            if (binding.etName.text.isNullOrEmpty() && state.user != null) {
                binding.etName.setText(state.user.fullName)
                binding.etPhone.setText(state.user.phone)
                binding.ivAvatar.loadImage(state.user.photoUrl.ifBlank { null })
            }

            binding.progress.isVisible = state.saving
            binding.btnSave.isEnabled = !state.saving
            binding.tilName.error = state.nameError

            if (state.formError != null) {
                binding.tvFormError.isVisible = true
                binding.tvFormError.text = state.formError
            } else {
                binding.tvFormError.isVisible = false
            }

            if (state.saveSuccess) {
                viewModel.clearSaveSuccess()
                Snackbar.make(binding.root, R.string.profile_updated, Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
