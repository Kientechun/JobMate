package com.example.jobmate.data.repository

import com.example.jobmate.data.models.*
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResumeRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getMyResumes(): Flow<Resource<List<ResumeDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMyResumes()
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy danh sách CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getResumeDetail(id: Int): Flow<Resource<ResumeDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getResumeDetail(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy chi tiết CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun createResume(request: CreateResumeRequestDTO): Flow<Resource<ResumeDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createResume(request)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi tạo CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun updateResume(id: Int, request: CreateResumeRequestDTO): Flow<Resource<ResumeDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateResume(id, request)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi cập nhật CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun deleteResume(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteResume(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi xóa CV"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun setDefaultResume(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.setDefaultResume(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi đặt CV mặc định"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}
