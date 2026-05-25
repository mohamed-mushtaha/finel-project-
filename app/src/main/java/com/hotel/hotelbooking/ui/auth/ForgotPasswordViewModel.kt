package com.hotel.hotelbooking.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordState(
    val loading: Boolean = false,
    val emailError: String? = null,
    val formError: String? = null,
    val sentToEmail: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableLiveData(ForgotPasswordState())
    val state: LiveData<ForgotPasswordState> = _state

    fun submit(email: String) {
        if (email.isBlank() || !email.contains("@")) {
            _state.value = ForgotPasswordState(emailError = "Enter a valid email")
            return
        }
        _state.value = ForgotPasswordState(loading = true)
        viewModelScope.launch {
            authRepository.sendPasswordReset(email.trim())
                .onSuccess { _state.postValue(ForgotPasswordState(sentToEmail = email.trim())) }
                .onFailure { e -> _state.postValue(ForgotPasswordState(formError = e.message ?: "Failed to send reset email")) }
        }
    }
}
