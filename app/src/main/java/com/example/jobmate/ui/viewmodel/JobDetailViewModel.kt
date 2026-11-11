package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.ApplicationDTO
import com.example.jobmate.data.models.CreateApplicationRequestDTO
import com.example.jobmate.data.models.Job
import com.example.jobmate.data.repository.ApplicationRepository
import com.example.jobmate.data.repository.JobRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val applicationRepository: ApplicationRepository
) : ViewModel() {

    private val _jobDetail = MutableStateFlow<Resource<Job>>(Resource.Empty())
    val jobDetail: StateFlow<Resource<Job>> = _jobDetail

    private val _applyState = MutableStateFlow<Resource<ApplicationDTO>>(Resource.Empty())
    val applyState: StateFlow<Resource<ApplicationDTO>> = _applyState

    fun getJobDetail(jobId: Int) {
        viewModelScope.launch {
            jobRepository.getJobDetail(jobId).collect {
                _jobDetail.value = it
            }
        }
    }

    fun applyJob(request: CreateApplicationRequestDTO) {
        viewModelScope.launch {
            applicationRepository.applyJob(request).collect {
                _applyState.value = it
            }
        }
    }
}
