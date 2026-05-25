package com.hotel.hotelbooking.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.databinding.FragmentPropertyFormBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomPadding
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PropertyFormFragment : Fragment() {

    private var _binding: FragmentPropertyFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PropertyFormViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.setImageUri(it)
            binding.ivImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPropertyFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.bottomBar.applySystemBarBottomPadding()
        binding.fabPickImage.setOnClickListener { pickImage.launch("image/*") }

        binding.btnSave.setOnClickListener { submitForm() }

        viewModel.state.observe(viewLifecycleOwner) { s ->
            // Pre-fill when editing
            s.property?.let { p ->
                if (binding.etName.text.isNullOrBlank()) {
                    binding.etName.setText(p.name)
                    binding.etAddress.setText(p.address)
                    binding.etDescription.setText(p.description)
                    binding.etLat.setText(if (p.location.latitude != 0.0) p.location.latitude.toString() else "")
                    binding.etLng.setText(if (p.location.longitude != 0.0) p.location.longitude.toString() else "")
                    binding.ivImage.loadImage(p.imageUrl)
                    if (p.type == PropertyType.APARTMENT) binding.chipApartment.isChecked = true
                    else binding.chipHotel.isChecked = true
                    binding.toolbar.title = getString(R.string.edit_property)
                }
            }

            binding.tilName.error = s.nameError
            binding.tilAddress.error = s.addressError

            if (s.error != null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = s.error
            } else {
                binding.tvError.visibility = View.GONE
            }

            binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !s.loading

            if (s.saved) {
                Snackbar.make(binding.root, getString(R.string.property_saved), Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun submitForm() {
        val type = if (binding.chipApartment.isChecked) PropertyType.APARTMENT else PropertyType.HOTEL
        viewModel.save(
            name = binding.etName.text?.toString().orEmpty(),
            address = binding.etAddress.text?.toString().orEmpty(),
            latStr = binding.etLat.text?.toString().orEmpty(),
            lngStr = binding.etLng.text?.toString().orEmpty(),
            description = binding.etDescription.text?.toString().orEmpty(),
            type = type
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
