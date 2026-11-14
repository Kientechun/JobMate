package com.example.jobmate.data.repository

import com.example.jobmate.data.models.Job
import com.example.jobmate.data.models.JobDTO
import com.example.jobmate.data.models.toDomain
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getJobs(page: Int, pageSize: Int = 20, query: String? = null): Flow<Resource<List<Job>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getJobs(pageIndex = page, pageSize = pageSize, keywords = query)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                val jobsDTO = body.data?.items
                if (jobsDTO.isNullOrEmpty()) {
                    emit(Resource.Empty())
                } else {
                    emit(Resource.Success(jobsDTO.map { it.toDomain() }))
                }
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách công việc"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "Lỗi máy chủ"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getJobDetail(jobId: Int): Flow<Resource<Job>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getJobDetail(jobId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                body.data?.let {
                    emit(Resource.Success(it.toDomain()))
                } ?: emit(Resource.Error("Không tìm thấy dữ liệu"))
            } else {
                emit(Resource.Error(body?.message ?: "Không tìm thấy thông tin công việc"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        } catch (e: HttpException) {
            emit(Resource.Error(e.message() ?: "Lỗi máy chủ"))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getSavedJobs(page: Int, pageSize: Int = 20): Flow<Resource<List<Job>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getSavedJobs(pageIndex = page, pageSize = pageSize)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                val jobsDTO = body.data?.items
                if (jobsDTO.isNullOrEmpty()) {
                    emit(Resource.Empty())
                } else {
                    emit(Resource.Success(jobsDTO.map { it.toDomain() }))
                }
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách việc làm đã lưu"))
            }
        } catch (e: IOException) {
            emit(Resource.Error("Lỗi kết nối mạng"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun saveJob(jobId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.saveJob(jobId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Không thể lưu việc làm"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun unsaveJob(jobId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.unsaveJob(jobId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Không thể bỏ lưu việc làm"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }
}
