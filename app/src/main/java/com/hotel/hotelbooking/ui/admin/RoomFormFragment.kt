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
import com.hotel.hotelbooking.databinding.FragmentRoomFormBinding
import com.hotel.hotelbooking.ui.util.applySystemBarBottomPadding
import com.hotel.hotelbooking.ui.util.loadImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoomFormFragment : Fragment() {

    private var _binding: FragmentRoomFormBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RoomFormViewModel by viewModels()

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.setImageUri(it)
            binding.ivImage.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoomFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.bottomBar.applySystemBarBottomPadding()
        binding.fabPickImage.setOnClickListener { pickImage.launch("image/*") }
        binding.btnSave.setOnClickListener { submitForm() }

        viewModel.state.observe(viewLifecycleOwner) { s ->
            s.room?.let { room ->
                if (binding.etName.text.isNullOrBlank()) {
                    binding.etName.setText(room.name)
                    binding.etDescription.setText(room.description)
                    binding.etPrice.setText(room.pricePerNight.toString())
                    binding.etCapacity.setText(room.capacity.toString())
                    binding.etAmenities.setText(room.amenities.joinToString(", "))
                    binding.switchAvailable.isChecked = room.available
                    binding.ivImage.loadImage(room.imageUrl)
                    binding.toolbar.title = getString(R.string.edit_room)
                }
            }

            binding.tilName.error = s.nameError
            binding.tilPrice.error = s.priceError

            if (s.error != null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = s.error
            } else {
                binding.tvError.visibility = View.GONE
            }

            binding.progress.visibility = if (s.loading) View.VISIBLE else View.GONE
            binding.btnSave.isEnabled = !s.loading

            if (s.saved) {
                Snackbar.make(binding.root, getString(R.string.room_saved), Snackbar.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun submitForm() {
        viewModel.save(
            name = binding.etName.text?.toString().orEmpty(),
            description = binding.etDescription.text?.toString().orEmpty(),
            priceStr = binding.etPrice.text?.toString().orEmpty(),
            capacityStr = binding.etCapacity.text?.toString().orEmpty(),
            amenitiesStr = binding.etAmenities.text?.toString().orEmpty(),
            available = binding.switchAvailable.isChecked
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
