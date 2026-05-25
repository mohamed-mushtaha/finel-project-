package com.hotel.hotelbooking.ui.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class PropertyMapViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val propertyRepository: PropertyRepository
) : ViewModel() {

    private val propertyId: String = checkNotNull(savedStateHandle["propertyId"])

    val property: LiveData<Resource<Property?>> =
        propertyRepository.observeById(propertyId)
            .map<Property?, Resource<Property?>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error")) }
            .asLiveData()
}
