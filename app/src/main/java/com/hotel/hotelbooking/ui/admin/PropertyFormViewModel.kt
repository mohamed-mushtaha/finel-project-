package com.hotel.hotelbooking.ui.admin

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.GeoPoint
import com.hotel.hotelbooking.data.model.Property
import com.hotel.hotelbooking.data.model.PropertyType
import com.hotel.hotelbooking.domain.repository.AuthRepository
import com.hotel.hotelbooking.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PropertyFormState(
    val property: Property? = null,
    val selectedImageUri: Uri? = null,
    val loading: Boolean = false,
    val saved: Boolean = false,
    val nameError: String? = null,
    val addressError: String? = null,
    val error: String? = null
)

@HiltViewModel
class PropertyFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val propertyRepository: PropertyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val propertyId: String = savedStateHandle["propertyId"] ?: ""
    private val isEdit: Boolean get() = propertyId.isNotBlank()

    private val _state = MutableLiveData(PropertyFormState())
    val state: LiveData<PropertyFormState> = _state

    init {
        if (isEdit) loadProperty()
    }

    private fun loadProperty() {
        viewModelScope.launch {
            val property = propertyRepository.observeById(propertyId).first()
            _state.value = _state.value!!.copy(property = property)
        }
    }

    fun setImageUri(uri: Uri) {
        _state.value = _state.value!!.copy(selectedImageUri = uri)
    }

    fun save(
        name: String,
        address: String,
        latStr: String,
        lngStr: String,
        description: String,
        type: PropertyType
    ) {
        val s = _state.value!!
        var nameErr: String? = null
        var addressErr: String? = null

        if (name.isBlank()) nameErr = "Property name is required"
        if (address.isBlank()) addressErr = "Address is required"

        if (nameErr != null || addressErr != null) {
            _state.value = s.copy(nameError = nameErr, addressError = addressErr)
            return
        }

        val lat = latStr.toDoubleOrNull() ?: 0.0
        val lng = lngStr.toDoubleOrNull() ?: 0.0

        _state.value = s.copy(loading = true, nameError = null, addressError = null, error = null)
        viewModelScope.launch {
            val userId = authRepository.currentUser.first()?.uid ?: ""
            val existing = s.property

            val property = (existing ?: Property()).copy(
                ownerId = userId,
                name = name.trim(),
                address = address.trim(),
                location = GeoPoint(lat, lng),
                description = description.trim(),
                type = type
            )

            val result = if (isEdit)
                propertyRepository.update(property, s.selectedImageUri)
            else
                propertyRepository.create(property, s.selectedImageUri)

            result
                .onSuccess { _state.value = s.copy(loading = false, saved = true) }
                .onFailure { _state.value = s.copy(loading = false, error = it.message ?: "Save failed") }
        }
    }
}
