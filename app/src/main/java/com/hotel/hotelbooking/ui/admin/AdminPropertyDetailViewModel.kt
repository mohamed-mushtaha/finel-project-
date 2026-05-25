package com.hotel.hotelbooking.ui.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.data.util.Resource
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminPropertyDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val propertyRepository: PropertyRepository,
    private val roomRepository: RoomRepository
) : ViewModel() {

    val propertyId: String = checkNotNull(savedStateHandle["propertyId"])

    val property: LiveData<Resource<Property?>> =
        propertyRepository.observeById(propertyId)
            .map<Property?, Resource<Property?>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error")) }
            .asLiveData()

    val rooms: LiveData<Resource<List<Room>>> =
        roomRepository.observeByProperty(propertyId)
            .map<List<Room>, Resource<List<Room>>> { Resource.Success(it) }
            .onStart { emit(Resource.Loading) }
            .catch { emit(Resource.Error(it.message ?: "Error")) }
            .asLiveData()

    private val _deleteResult = MutableLiveData<Result<Unit>?>()
    val deleteResult: LiveData<Result<Unit>?> = _deleteResult

    fun deleteProperty() {
        viewModelScope.launch {
            _deleteResult.value = propertyRepository.delete(propertyId)
        }
    }

    fun deleteRoom(roomId: String) {
        viewModelScope.launch { roomRepository.delete(roomId) }
    }

    fun consumeDeleteResult() { _deleteResult.value = null }
}
