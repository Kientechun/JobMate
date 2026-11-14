package com.example.jobmate.data.repository

import com.example.jobmate.data.models.InterviewDTO
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InterviewRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getMyInterviews(): Flow<Resource<List<InterviewDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMyInterviews()
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: emptyList()))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách lịch phỏng vấn"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getInterviewDetail(id: Int): Flow<Resource<InterviewDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getInterviewDetail(id)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true && body.data != null) {
                emit(Resource.Success(body.data))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy chi tiết lịch phỏng vấn"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}
