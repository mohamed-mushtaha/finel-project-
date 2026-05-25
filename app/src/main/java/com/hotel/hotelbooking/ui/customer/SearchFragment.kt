package com.hotel.hotelbooking.ui.customer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hotel.hotelbooking.R
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val adapter = RoomAdapter { room ->
        findNavController().navigate(
            SearchFragmentDirections.actionSearchToRoomDetail(
                roomId = room.id,
                propertyId = room.propertyId
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.setQuery(s?.toString().orEmpty())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) { applyFilters(); true } else false
        }

        binding.chipGroupRating.setOnCheckedStateChangeListener { _, _ -> applyFilters() }
        binding.etMinPrice.addTextChangedListener(simpleTextWatcher { applyFilters() })
        binding.etMaxPrice.addTextChangedListener(simpleTextWatcher { applyFilters() })

        viewModel.results.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvResults.visibility = View.GONE
                }
                is Resource.Success -> {
                    binding.progress.visibility = View.GONE
                    adapter.submitList(resource.data)
                    val isEmpty = resource.data.isEmpty()
                    val query = binding.etSearch.text?.toString().orEmpty()
                    binding.layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
                    binding.tvEmptyMessage.text = when {
                        isEmpty && query.isBlank() -> getString(R.string.search_empty_hint)
                        isEmpty -> getString(R.string.search_no_results, query)
                        else -> ""
                    }
                    binding.rvResults.visibility = if (isEmpty) View.GONE else View.VISIBLE
                }
                is Resource.Error -> {
                    binding.progress.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE
                    binding.rvResults.visibility = View.GONE
                    binding.tvEmptyMessage.text = getString(R.string.something_went_wrong)
                }
            }
        }
    }

    private fun applyFilters() {
        val minRating = when (binding.chipGroupRating.checkedChipId) {
            R.id.chip_rating_3 -> 3f
            R.id.chip_rating_4 -> 4f
            R.id.chip_rating_5 -> 5f
            else -> 0f
        }
        val minPrice = binding.etMinPrice.text?.toString()?.toDoubleOrNull()
        val maxPrice = binding.etMaxPrice.text?.toString()?.toDoubleOrNull()
        viewModel.setFilters(minRating, minPrice, maxPrice)
    }

    private fun simpleTextWatcher(action: () -> Unit) = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) = action()
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
