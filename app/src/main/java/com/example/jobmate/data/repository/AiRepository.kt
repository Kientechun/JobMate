package com.example.jobmate.data.repository

import com.example.jobmate.data.models.JobRecommendationDTO
import com.example.jobmate.data.models.ResumeAnalysisDTO
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun analyzeResume(resumeId: Int): Flow<Resource<ResumeAnalysisDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.analyzeResume(resumeId)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi phân tích CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getResumeAnalysis(resumeId: Int): Flow<Resource<ResumeAnalysisDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getResumeAnalysis(resumeId)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy kết quả phân tích CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getJobRecommendations(resumeId: Int? = null): Flow<Resource<List<JobRecommendationDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getJobRecommendations(resumeId)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy gợi ý việc làm"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}
