package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.*
import com.example.jobmate.data.repository.RecruiterRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecruiterViewModel @Inject constructor(
    private val repository: RecruiterRepository
) : ViewModel() {

    private val _dashboard = MutableStateFlow<Resource<RecruiterDashboardDTO>>(Resource.Loading())
    val dashboard: StateFlow<Resource<RecruiterDashboardDTO>> = _dashboard

    private val _myJobs = MutableStateFlow<Resource<PaginatedListDTO<JobDTO>>>(Resource.Loading())
    val myJobs: StateFlow<Resource<PaginatedListDTO<JobDTO>>> = _myJobs

    private val _applications = MutableStateFlow<Resource<PaginatedListDTO<ApplicationDTO>>>(Resource.Loading())
    val applications: StateFlow<Resource<PaginatedListDTO<ApplicationDTO>>> = _applications

    private val _jobActionState = MutableStateFlow<Resource<JobDTO>?>(null)
    val jobActionState: StateFlow<Resource<JobDTO>?> = _jobActionState

    private val _statusUpdateState = MutableStateFlow<Resource<Boolean>?>(null)
    val statusUpdateState: StateFlow<Resource<Boolean>?> = _statusUpdateState

    fun getDashboard() {
        viewModelScope.launch {
            repository.getDashboard().collectLatest { _dashboard.value = it }
        }
    }

    fun getMyJobs(page: Int = 1) {
        viewModelScope.launch {
            repository.getMyJobs(page).collectLatest { _myJobs.value = it }
        }
    }

    fun createJob(request: CreateJobRequestDTO) {
        viewModelScope.launch {
            repository.createJob(request).collectLatest { _jobActionState.value = it }
        }
    }

    fun updateJob(id: Int, request: CreateJobRequestDTO) {
        viewModelScope.launch {
            repository.updateJob(id, request).collectLatest { _jobActionState.value = it }
        }
    }

    fun closeJob(id: Int) {
        viewModelScope.launch {
            repository.closeJob(id).collectLatest { resource ->
                if (resource is Resource.Success) {
                    getMyJobs() // Refresh
                }
            }
        }
    }

    fun getApplications(jobId: Int? = null, page: Int = 1) {
        viewModelScope.launch {
            repository.getApplications(jobId, page).collectLatest { _applications.value = it }
        }
    }

    fun updateApplicationStatus(id: Int, status: Int, note: String) {
        viewModelScope.launch {
            repository.updateApplicationStatus(id, status, note).collectLatest { 
                _statusUpdateState.value = it 
            }
        }
    }

    fun resetJobActionState() { _jobActionState.value = null }
    fun resetStatusUpdateState() { _statusUpdateState.value = null }
}
