package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val _filterType = MutableStateFlow<PropertyType?>(null)
    private val _refresh = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val properties: LiveData<Resource<List<Property>>> =
        combine(_filterType, _refresh) { type, _ -> type }
            .flatMapLatest { type ->
                propertyRepository.observeAll(type)
                    .map<List<Property>, Resource<List<Property>>> { Resource.Success(it) }
                    .onStart { emit(Resource.Loading) }
                    .catch { emit(Resource.Error(it.message ?: "Error loading properties")) }
            }
            .asLiveData()

    fun setFilter(type: PropertyType?) {
        _filterType.value = type
    }

    fun refresh() {
        _refresh.value++
    }
}
