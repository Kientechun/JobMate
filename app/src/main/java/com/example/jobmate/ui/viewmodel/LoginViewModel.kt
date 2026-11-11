package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.AuthResponse
import com.example.jobmate.data.models.LoginRequestDTO
import com.example.jobmate.data.repository.AuthRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<AuthResponse>>(Resource.Empty())
    val loginState: StateFlow<Resource<AuthResponse>> = _loginState

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = Resource.Error("Vui lòng nhập đầy đủ thông tin")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _loginState.value = Resource.Error("Email không hợp lệ")
            return
        }
        viewModelScope.launch {
            authRepository.login(LoginRequestDTO(email, pass)).collect {
                _loginState.value = it
            }
        }
    }
}
