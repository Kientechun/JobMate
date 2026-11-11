package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.AuthResponse
import com.example.jobmate.data.models.RegisterRequestDTO
import com.example.jobmate.data.repository.AuthRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _registerState = MutableStateFlow<Resource<AuthResponse>>(Resource.Empty())
    val registerState: StateFlow<Resource<AuthResponse>> = _registerState

    fun register(email: String, pass: String, fullName: String, role: String) {
        if (email.isBlank() || pass.isBlank() || fullName.isBlank()) {
            _registerState.value = Resource.Error("Vui lòng điền đầy đủ thông tin")
            return
        }
        viewModelScope.launch {
            authRepository.register(RegisterRequestDTO(email, pass, fullName, role)).collect {
                _registerState.value = it
            }
        }
    }
}
