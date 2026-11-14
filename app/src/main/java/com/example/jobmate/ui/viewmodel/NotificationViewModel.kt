package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.NotificationDTO
import com.example.jobmate.data.repository.NotificationRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<Resource<List<NotificationDTO>>>(Resource.Empty())
    val notifications: StateFlow<Resource<List<NotificationDTO>>> = _notifications

    fun fetchNotifications() {
        viewModelScope.launch {
            repository.getNotifications().collect {
                _notifications.value = it
            }
        }
    }

    fun markAsRead(id: Int) {
        viewModelScope.launch {
            repository.markAsRead(id).collect {
                if (it is Resource.Success) {
                    fetchNotifications()
                }
            }
        }
    }
}
