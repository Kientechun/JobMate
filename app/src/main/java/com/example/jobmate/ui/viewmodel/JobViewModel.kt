package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.Job
import com.example.jobmate.data.repository.JobRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _jobsState = MutableStateFlow<Resource<List<Job>>>(Resource.Empty())
    val jobsState: StateFlow<Resource<List<Job>>> = _jobsState

    fun fetchJobs(page: Int, query: String? = null) {
        viewModelScope.launch {
            jobRepository.getJobs(page, 20, query).collect {
                _jobsState.value = it
            }
        }
    }

    private val _savedJobsState = MutableStateFlow<Resource<List<Job>>>(Resource.Empty())
    val savedJobsState: StateFlow<Resource<List<Job>>> = _savedJobsState

    fun fetchSavedJobs(page: Int) {
        viewModelScope.launch {
            jobRepository.getSavedJobs(page).collect {
                _savedJobsState.value = it
            }
        }
    }

    private val _saveJobStatus = MutableStateFlow<Resource<Boolean>>(Resource.Empty())
    val saveJobStatus: StateFlow<Resource<Boolean>> = _saveJobStatus

    fun saveJob(jobId: Int) {
        viewModelScope.launch {
            jobRepository.saveJob(jobId).collect {
                _saveJobStatus.value = it
            }
        }
    }

    fun unsaveJob(jobId: Int) {
        viewModelScope.launch {
            jobRepository.unsaveJob(jobId).collect {
                _saveJobStatus.value = it
            }
        }
    }

    fun resetSaveStatus() {
        _saveJobStatus.value = Resource.Empty()
    }
}
