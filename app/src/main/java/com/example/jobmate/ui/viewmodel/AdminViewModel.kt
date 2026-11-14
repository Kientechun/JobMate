package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.*
import com.example.jobmate.data.repository.AdminRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: AdminRepository
) : ViewModel() {

    private val _dashboard = MutableStateFlow<Resource<AdminDashboardDTO>>(Resource.Loading())
    val dashboard: StateFlow<Resource<AdminDashboardDTO>> = _dashboard

    private val _users = MutableStateFlow<Resource<PaginatedListDTO<UserManagementDTO>>>(Resource.Loading())
    val users: StateFlow<Resource<PaginatedListDTO<UserManagementDTO>>> = _users

    private val _pendingJobs = MutableStateFlow<Resource<PaginatedListDTO<JobDTO>>>(Resource.Loading())
    val pendingJobs: StateFlow<Resource<PaginatedListDTO<JobDTO>>> = _pendingJobs

    private val _categories = MutableStateFlow<Resource<List<CategoryDTO>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<CategoryDTO>>> = _categories

    private val _actionState = MutableStateFlow<Resource<Boolean>?>(null)
    val actionState: StateFlow<Resource<Boolean>?> = _actionState

    fun getDashboard() {
        viewModelScope.launch {
            repository.getDashboard().collectLatest { _dashboard.value = it }
        }
    }

    fun getCategories() {
        viewModelScope.launch {
            repository.getCategories().collectLatest { _categories.value = it }
        }
    }

    fun getUsers(page: Int = 1) {
        viewModelScope.launch {
            repository.getUsers(page).collectLatest { _users.value = it }
        }
    }

    fun lockUser(userId: String) {
        viewModelScope.launch {
            repository.lockUser(userId).collectLatest { resource ->
                _actionState.value = resource
                if (resource is Resource.Success) getUsers()
            }
        }
    }

    fun unlockUser(userId: String) {
        viewModelScope.launch {
            repository.unlockUser(userId).collectLatest { resource ->
                _actionState.value = resource
                if (resource is Resource.Success) getUsers()
            }
        }
    }

    fun getPendingJobs(page: Int = 1) {
        viewModelScope.launch {
            repository.getPendingJobs(page).collectLatest { _pendingJobs.value = it }
        }
    }

    fun approveJob(jobId: Int) {
        viewModelScope.launch {
            repository.approveJob(jobId).collectLatest { resource ->
                _actionState.value = resource
                if (resource is Resource.Success) getPendingJobs()
            }
        }
    }

    fun rejectJob(jobId: Int, reason: String) {
        viewModelScope.launch {
            repository.rejectJob(jobId, reason).collectLatest { resource ->
                _actionState.value = resource
                if (resource is Resource.Success) getPendingJobs()
            }
        }
    }

    fun createCategory(name: String) {
        viewModelScope.launch {
            repository.createCategory(name).collectLatest { resource ->
                _actionState.value = when (resource) {
                    is Resource.Success -> {
                        getCategories()
                        Resource.Success(true)
                    }
                    is Resource.Error -> Resource.Error(resource.message!!)
                    else -> Resource.Loading()
                }
            }
        }
    }

    fun updateCategory(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateCategory(id, name).collectLatest { resource ->
                _actionState.value = when (resource) {
                    is Resource.Success -> {
                        getCategories()
                        Resource.Success(true)
                    }
                    is Resource.Error -> Resource.Error(resource.message!!)
                    else -> Resource.Loading()
                }
            }
        }
    }

    fun deleteCategory(id: Int) {
        viewModelScope.launch {
            repository.deleteCategory(id).collectLatest { resource ->
                _actionState.value = resource
                if (resource is Resource.Success) getCategories()
            }
        }
    }

    fun resetActionState() { _actionState.value = null }
}
