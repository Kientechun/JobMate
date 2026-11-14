package com.example.jobmate.data.repository

import com.example.jobmate.data.models.*
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getDashboard(): Flow<Resource<AdminDashboardDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAdminDashboard()
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy dữ liệu dashboard admin"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getUsers(page: Int, pageSize: Int = 20): Flow<Resource<PaginatedListDTO<UserManagementDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getUsers(page, pageSize)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách người dùng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun lockUser(userId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.lockUser(userId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi khóa tài khoản"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun unlockUser(userId: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.unlockUser(userId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi mở khóa tài khoản"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getPendingJobs(page: Int, pageSize: Int = 20): Flow<Resource<PaginatedListDTO<JobDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getPendingJobs(page, pageSize)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách tin chờ duyệt"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun approveJob(jobId: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.approveJob(jobId)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi duyệt tin"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun rejectJob(jobId: Int, reason: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.rejectJob(jobId, RejectJobRequestDTO(reason))
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi từ chối tin"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun createCategory(name: String): Flow<Resource<CategoryDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createCategory(CategoryRequestDTO(name))
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi tạo danh mục"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun updateCategory(id: Int, name: String): Flow<Resource<CategoryDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateCategory(id, CategoryRequestDTO(name))
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data!!))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi cập nhật danh mục"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun deleteCategory(id: Int): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteCategory(id)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: true))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi xóa danh mục"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun getCategories(): Flow<Resource<List<CategoryDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCategories()
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: emptyList()))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách danh mục"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }
}
