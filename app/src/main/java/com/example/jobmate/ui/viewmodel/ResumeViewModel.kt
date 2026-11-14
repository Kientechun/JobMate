package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.CreateResumeRequestDTO
import com.example.jobmate.data.models.ResumeDTO
import com.example.jobmate.data.repository.ResumeRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResumeViewModel @Inject constructor(
    private val repository: ResumeRepository
) : ViewModel() {

    private val _resumes = MutableStateFlow<Resource<List<ResumeDTO>>>(Resource.Loading())
    val resumes: StateFlow<Resource<List<ResumeDTO>>> = _resumes.asStateFlow()

    private val _resumeDetail = MutableStateFlow<Resource<ResumeDTO>?>(null)
    val resumeDetail: StateFlow<Resource<ResumeDTO>?> = _resumeDetail.asStateFlow()

    private val _createStatus = MutableStateFlow<Resource<ResumeDTO>?>(null)
    val createStatus: StateFlow<Resource<ResumeDTO>?> = _createStatus.asStateFlow()

    init {
        fetchResumes()
    }

    fun fetchResumes() {
        viewModelScope.launch {
            repository.getMyResumes().collect {
                _resumes.value = it
            }
        }
    }

    fun fetchResumeDetail(id: Int) {
        viewModelScope.launch {
            repository.getResumeDetail(id).collect {
                _resumeDetail.value = it
            }
        }
    }

    fun createResume(request: CreateResumeRequestDTO) {
        viewModelScope.launch {
            repository.createResume(request).collect {
                _createStatus.value = it
                if (it is Resource.Success) {
                    fetchResumes()
                }
            }
        }
    }

    fun updateResume(id: Int, request: CreateResumeRequestDTO) {
        viewModelScope.launch {
            repository.updateResume(id, request).collect {
                _createStatus.value = it
                if (it is Resource.Success) {
                    fetchResumes()
                }
            }
        }
    }

    fun deleteResume(id: Int) {
        viewModelScope.launch {
            repository.deleteResume(id).collect {
                if (it is Resource.Success) {
                    fetchResumes()
                }
            }
        }
    }

    fun setDefaultResume(id: Int) {
        viewModelScope.launch {
            repository.setDefaultResume(id).collect {
                if (it is Resource.Success) {
                    fetchResumes()
                }
            }
        }
    }

    private val _uploadStatus = MutableStateFlow<Resource<String>?>(null)
    val uploadStatus: StateFlow<Resource<String>?> = _uploadStatus.asStateFlow()

    fun uploadResume(filePart: okhttp3.MultipartBody.Part) {
        viewModelScope.launch {
            repository.uploadResume(filePart).collect {
                _uploadStatus.value = it
            }
        }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = null
    }
}
