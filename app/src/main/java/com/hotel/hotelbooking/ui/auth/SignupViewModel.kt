package com.hotel.hotelbooking.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hotel.hotelbooking.data.model.UserRole
import com.hotel.hotelbooking.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignupUiState(
    val loading: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val navigateTo: UserRole? = null
)

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(SignupUiState())
    val uiState: LiveData<SignupUiState> = _uiState

    var selectedRole: UserRole = UserRole.CUSTOMER

    fun submit(fullName: String, email: String, phone: String, password: String) {
        val nameErr = if (fullName.isBlank()) "Required" else null
        val emailErr = if (email.isBlank() || !email.contains("@")) "Enter a valid email" else null
        val passErr = if (password.length < 6) "At least 6 characters" else null
        if (nameErr != null || emailErr != null || passErr != null) {
            _uiState.value = SignupUiState(nameError = nameErr, emailError = emailErr, passwordError = passErr)
            return
        }
        _uiState.value = SignupUiState(loading = true)
        viewModelScope.launch {
            authRepository.signUp(
                email = email.trim(),
                password = password,
                fullName = fullName.trim(),
                phone = phone.trim(),
                role = selectedRole
            )
                .onSuccess { user -> _uiState.postValue(SignupUiState(navigateTo = user.role)) }
                .onFailure { e -> _uiState.postValue(SignupUiState(formError = e.message ?: "Sign up failed")) }
        }
    }

    fun clearNavigation() {
        _uiState.value = _uiState.value?.copy(navigateTo = null)
    }
}
