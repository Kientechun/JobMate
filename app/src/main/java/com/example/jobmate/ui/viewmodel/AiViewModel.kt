package com.example.jobmate.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jobmate.data.models.JobRecommendationDTO
import com.example.jobmate.data.models.ResumeAnalysisDTO
import com.example.jobmate.data.repository.AiRepository
import com.example.jobmate.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiViewModel @Inject constructor(
    private val repository: AiRepository
) : ViewModel() {

    private val _analysis = MutableStateFlow<Resource<ResumeAnalysisDTO>>(Resource.Empty())
    val analysis: StateFlow<Resource<ResumeAnalysisDTO>> = _analysis

    private val _recommendations = MutableStateFlow<Resource<List<JobRecommendationDTO>>>(Resource.Empty())
    val recommendations: StateFlow<Resource<List<JobRecommendationDTO>>> = _recommendations

    fun analyzeResume(resumeId: Int) {
        viewModelScope.launch {
            repository.analyzeResume(resumeId).collect {
                _analysis.value = it
            }
        }
    }

    fun fetchAnalysis(resumeId: Int) {
        viewModelScope.launch {
            repository.getResumeAnalysis(resumeId).collect {
                _analysis.value = it
            }
        }
    }

    fun fetchRecommendations(resumeId: Int? = null) {
        viewModelScope.launch {
            repository.getJobRecommendations(resumeId).collect {
                _recommendations.value = it
            }
        }
    }
}
