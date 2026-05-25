package com.hotel.hotelbooking.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.User
import com.hotel.hotelbooking.data.util.ImageHelper
import com.hotel.hotelbooking.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val loading: Boolean = false,
    val saving: Boolean = false,
    val nameError: String? = null,
    val formError: String? = null,
    val saveSuccess: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableLiveData(ProfileUiState(loading = true))
    val state: LiveData<ProfileUiState> = _state

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _state.postValue(_state.value?.copy(user = user, loading = false))
            }
        }
    }

    fun saveProfile(fullName: String, phone: String, avatarUri: Uri?) {
        if (fullName.isBlank()) {
            _state.value = _state.value?.copy(nameError = "Name is required")
            return
        }
        _state.value = _state.value?.copy(saving = true, nameError = null, formError = null)

        val currentPhotoPath = _state.value?.user?.photoUrl
        val newPhotoPath: String? = avatarUri?.let {
            // Delete old avatar before saving new one
            if (!currentPhotoPath.isNullOrBlank()) ImageHelper.delete(currentPhotoPath)
            ImageHelper.saveToInternal(context, it, "avatar_${System.currentTimeMillis()}")
        }

        viewModelScope.launch {
            authRepository.updateProfile(
                fullName = fullName.trim(),
                phone = phone.trim(),
                photoUrl = newPhotoPath
            )
                .onSuccess { user ->
                    _state.postValue(_state.value?.copy(saving = false, user = user, saveSuccess = true))
                }
                .onFailure { e ->
                    _state.postValue(_state.value?.copy(saving = false, formError = e.message ?: "Update failed"))
                }
        }
    }

    fun clearSaveSuccess() {
        _state.value = _state.value?.copy(saveSuccess = false)
    }
}
