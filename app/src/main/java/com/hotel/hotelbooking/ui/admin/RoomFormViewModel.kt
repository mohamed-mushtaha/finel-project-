package com.hotel.hotelbooking.ui.admin

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.Room
import com.hotel.hotelbooking.domain.repository.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RoomFormState(
    val room: Room? = null,
    val selectedImageUri: Uri? = null,
    val loading: Boolean = false,
    val saved: Boolean = false,
    val nameError: String? = null,
    val priceError: String? = null,
    val error: String? = null
)

@HiltViewModel
class RoomFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val roomRepository: RoomRepository
) : ViewModel() {

    private val propertyId: String = checkNotNull(savedStateHandle["propertyId"])
    private val roomId: String = savedStateHandle["roomId"] ?: ""
    private val isEdit: Boolean get() = roomId.isNotBlank()

    private val _state = MutableLiveData(RoomFormState())
    val state: LiveData<RoomFormState> = _state

    init {
        if (isEdit) loadRoom()
    }

    private fun loadRoom() {
        viewModelScope.launch {
            val room = roomRepository.observeById(roomId).first()
            _state.value = _state.value!!.copy(room = room)
        }
    }

    fun setImageUri(uri: Uri) {
        _state.value = _state.value!!.copy(selectedImageUri = uri)
    }

    fun save(
        name: String,
        description: String,
        priceStr: String,
        capacityStr: String,
        amenitiesStr: String,
        available: Boolean
    ) {
        val s = _state.value!!
        val nameErr = if (name.isBlank()) "Room name is required" else null
        val priceErr = if (priceStr.toDoubleOrNull() == null) "Enter a valid price" else null

        if (nameErr != null || priceErr != null) {
            _state.value = s.copy(nameError = nameErr, priceError = priceErr)
            return
        }

        val price = priceStr.toDouble()
        val capacity = capacityStr.toIntOrNull() ?: 2
        val amenities = amenitiesStr.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

        _state.value = s.copy(loading = true, nameError = null, priceError = null, error = null)
        viewModelScope.launch {
            val existing = s.room
            val room = (existing ?: Room()).copy(
                propertyId = propertyId,
                name = name.trim(),
                description = description.trim(),
                pricePerNight = price,
                capacity = capacity,
                amenities = amenities,
                available = available
            )

            val result = if (isEdit)
                roomRepository.update(room, s.selectedImageUri)
            else
                roomRepository.create(room, s.selectedImageUri)

            result
                .onSuccess { _state.value = s.copy(loading = false, saved = true) }
                .onFailure { _state.value = s.copy(loading = false, error = it.message ?: "Save failed") }
        }
    }
}
