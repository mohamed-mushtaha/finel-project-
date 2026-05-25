package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchFilters(
    val query: String = "",
    val minRating: Float = 0f,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
) {
    val isBlank: Boolean get() = query.isBlank() && minRating == 0f && minPrice == null && maxPrice == null
}

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val _results = MutableLiveData<Resource<List<Room>>>(Resource.Success(emptyList()))
    val results: LiveData<Resource<List<Room>>> = _results

    private val queryFlow = MutableStateFlow("")
    private val filtersFlow = MutableStateFlow(SearchFilters())

    init {
        viewModelScope.launch {
            queryFlow
                .debounce(300)
                .combine(filtersFlow) { q, f -> f.copy(query = q) }
                .collectLatest { filters ->
                    if (filters.isBlank) {
                        _results.postValue(Resource.Success(emptyList()))
                        return@collectLatest
                    }
                    roomRepository.search(
                        query = filters.query,
                        minRating = filters.minRating.toDouble().takeIf { it > 0.0 },
                        minPrice = filters.minPrice,
                        maxPrice = filters.maxPrice
                    )
                        .map<List<Room>, Resource<List<Room>>> { Resource.Success(it) }
                        .onStart { emit(Resource.Loading) }
                        .catch { emit(Resource.Error(it.message ?: "Search error")) }
                        .collectLatest { _results.postValue(it) }
                }
        }
    }

    fun setQuery(query: String) { queryFlow.value = query }

    fun setFilters(minRating: Float, minPrice: Double?, maxPrice: Double?) {
        filtersFlow.value = filtersFlow.value.copy(
            minRating = minRating,
            minPrice = minPrice,
            maxPrice = maxPrice
        )
    }
}
