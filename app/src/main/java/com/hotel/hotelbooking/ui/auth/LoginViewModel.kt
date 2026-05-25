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

data class LoginUiState(
    val loading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val formError: String? = null,
    val navigateTo: UserRole? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(LoginUiState())
    val uiState: LiveData<LoginUiState> = _uiState

    fun submit(email: String, password: String) {
        val emailErr = if (email.isBlank() || !email.contains("@")) "Enter a valid email" else null
        val passErr = if (password.length < 6) "At least 6 characters" else null
        if (emailErr != null || passErr != null) {
            _uiState.value = LoginUiState(emailError = emailErr, passwordError = passErr)
            return
        }
        _uiState.value = LoginUiState(loading = true)
        viewModelScope.launch {
            authRepository.signIn(email.trim(), password)
                .onSuccess { user -> _uiState.postValue(LoginUiState(navigateTo = user.role)) }
                .onFailure { e -> _uiState.postValue(LoginUiState(formError = e.message ?: "Sign in failed")) }
        }
    }

    fun clearNavigation() {
        _uiState.value = _uiState.value?.copy(navigateTo = null)
    }
}
