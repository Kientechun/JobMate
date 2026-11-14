package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.ApplicationDTO
import com.example.jobmate.data.models.ApplicationHistoryDTO
import com.example.jobmate.data.repository.ApplicationRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationViewModel @Inject constructor(
    private val repository: ApplicationRepository
) : ViewModel() {

    private val _applications = MutableStateFlow<Resource<List<ApplicationDTO>>>(Resource.Loading())
    val applications: StateFlow<Resource<List<ApplicationDTO>>> = _applications.asStateFlow()

    private val _filteredApplications = MutableStateFlow<List<ApplicationDTO>>(emptyList())
    val filteredApplications: StateFlow<List<ApplicationDTO>> = _filteredApplications.asStateFlow()

    private var allApplications: List<ApplicationDTO> = emptyList()
    private var currentFilter: String = "Tất cả"

    private val _applicationDetail = MutableStateFlow<Resource<ApplicationDTO>>(Resource.Empty())
    val applicationDetail: StateFlow<Resource<ApplicationDTO>> = _applicationDetail.asStateFlow()

    private val _applicationHistory = MutableStateFlow<Resource<List<ApplicationHistoryDTO>>>(Resource.Empty())
    val applicationHistory: StateFlow<Resource<List<ApplicationHistoryDTO>>> = _applicationHistory.asStateFlow()

    private val _withdrawStatus = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val withdrawStatus: StateFlow<Resource<Boolean>> = _withdrawStatus.asStateFlow()

    init {
        fetchApplications()
    }

    fun fetchApplications() {
        viewModelScope.launch {
            repository.getMyApplications().collect { resource ->
                _applications.value = resource
                if (resource is Resource.Success) {
                    allApplications = resource.data ?: emptyList()
                    applyFilter(currentFilter)
                }
            }
        }
    }

    fun getApplicationDetail(id: Int) {
        viewModelScope.launch {
            repository.getApplicationDetail(id).collect {
                _applicationDetail.value = it
            }
        }
    }

    fun getApplicationHistory(id: Int) {
        viewModelScope.launch {
            repository.getApplicationHistory(id).collect {
                _applicationHistory.value = it
            }
        }
    }

    fun applyFilter(status: String) {
        currentFilter = status
        _filteredApplications.value = if (status == "Tất cả") {
            allApplications
        } else {
            allApplications.filter { 
                when (status) {
                    "Chờ duyệt" -> it.status == 0 // Pending
                    "Đã duyệt" -> it.status == 1   // Approved
                    "Từ chối" -> it.status == 2    // Rejected
                    "Đang xem xét" -> it.status == 3 // UnderReview
                    else -> true
                }
            }
        }
    }

    fun withdrawApplication(id: Int) {
        viewModelScope.launch {
            repository.withdrawApplication(id).collect { resource ->
                _withdrawStatus.value = resource
                if (resource is Resource.Success) {
                    fetchApplications()
                }
            }
        }
    }
}
