package com.example.jobmate.data.repository

import com.example.jobmate.data.models.*
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApplicationRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun applyJob(request: CreateApplicationRequestDTO): Flow<Resource<ApplicationDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.applyJob(request)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi ứng tuyển"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getMyApplications(): Flow<Resource<List<ApplicationDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMyApplications()
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy danh sách ứng tuyển"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getApplicationDetail(id: Int): Flow<Resource<ApplicationDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getApplicationDetail(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data!!))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy chi tiết ứng tuyển"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun getApplicationHistory(id: Int): Flow<Resource<List<ApplicationHistoryDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getApplicationHistory(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(response.body()?.data ?: emptyList()))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi lấy lịch sử ứng tuyển"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun withdrawApplication(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.withdrawApplication(id)
            if (response.isSuccessful && response.body()?.succeeded == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Lỗi rút hồ sơ"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}
