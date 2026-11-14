package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.UpdateProfileRequestDTO
import com.example.jobmate.data.models.UserDTO
import com.example.jobmate.data.repository.AuthRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _logoutState = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val logoutState: StateFlow<Resource<Boolean>> = _logoutState

    private val _userProfile = MutableStateFlow<Resource<UserDTO>>(Resource.Empty())
    val userProfile: StateFlow<Resource<UserDTO>> = _userProfile

    private val _updateState = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val updateState: StateFlow<Resource<Boolean>> = _updateState

    fun fetchCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect {
                _userProfile.value = it
            }
        }
    }

    fun updateProfile(fullName: String, phoneNumber: String?) {
        viewModelScope.launch {
            authRepository.updateProfile(UpdateProfileRequestDTO(fullName, phoneNumber)).collect {
                _updateState.value = it
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().collect {
                _logoutState.value = it
            }
        }
    }
}