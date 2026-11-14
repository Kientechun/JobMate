package com.example.jobmate.data.repository

import com.example.jobmate.data.models.*
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecruiterRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getDashboard(): Flow<Resource<RecruiterDashboardDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecruiterDashboard()
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy dữ liệu dashboard"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getMyJobs(page: Int, pageSize: Int = 20): Flow<Resource<PaginatedListDTO<JobDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecruiterJobs(page, pageSize)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách tin tuyển dụng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun createJob(request: CreateJobRequestDTO): Flow<Resource<JobDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createJob(request)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi tạo tin tuyển dụng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun updateJob(id: Int, request: CreateJobRequestDTO): Flow<Resource<JobDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateJob(id, request)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi cập nhật tin tuyển dụng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun closeJob(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.closeJob(id)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi đóng tin tuyển dụng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getApplications(jobId: Int?, page: Int, pageSize: Int = 20): Flow<Resource<PaginatedListDTO<ApplicationDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getRecruiterApplications(jobId, page, pageSize)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách ứng viên"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun updateApplicationStatus(id: Int, status: Int, note: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateApplicationStatus(id, UpdateApplicationStatusRequestDTO(status, note))
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi cập nhật trạng thái"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }
}
