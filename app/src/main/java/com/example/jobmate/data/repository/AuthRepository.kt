package com.example.jobmate.data.repository

import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.data.models.AuthResponse
import com.example.jobmate.data.models.AuthResponseDTO
import com.example.jobmate.data.models.LoginRequestDTO
import com.example.jobmate.data.models.RegisterRequestDTO
import com.example.jobmate.data.models.UpdateProfileRequestDTO
import com.example.jobmate.data.models.User
import com.example.jobmate.data.models.UserDTO
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
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    fun login(request: LoginRequestDTO): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.login(request)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true && body.data != null) {
                val authResponseDTO = body.data!!
                tokenManager.saveTokens(
                    authResponseDTO.token,
                    authResponseDTO.refreshToken,
                    authResponseDTO.roles.firstOrNull() ?: "Candidate",
                    authResponseDTO.email,
                    authResponseDTO.fullName
                )
                emit(Resource.Success(authResponseDTO.toDomain()))
            } else {
                emit(Resource.Error(body?.message ?: body?.errors?.firstOrNull() ?: "Đăng nhập thất bại"))
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

    fun register(request: RegisterRequestDTO): Flow<Resource<AuthResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.register(request)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true && body.data != null) {
                val authResponseDTO = body.data!!
                tokenManager.saveTokens(
                    authResponseDTO.token,
                    authResponseDTO.refreshToken,
                    authResponseDTO.roles.firstOrNull() ?: "Candidate",
                    authResponseDTO.email,
                    authResponseDTO.fullName
                )
                emit(Resource.Success(authResponseDTO.toDomain()))
            } else {
                emit(Resource.Error(body?.message ?: body?.errors?.firstOrNull() ?: "Đăng ký thất bại"))
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

    suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.logout()
            if (response.isSuccessful) {
                tokenManager.clear()
                emit(Resource.Success(true))
            } else {
                // Vẫn xóa token local dù server fail để user thoát được
                tokenManager.clear()
                emit(Resource.Error("Đã đăng xuất local"))
            }
        } catch (e: Exception) {
            tokenManager.clear()
            emit(Resource.Error("Lỗi kết nối, đã xóa phiên làm việc"))
        }
    }
    
    fun getUserRole() = tokenManager.userRole

    fun getCurrentUser(): Flow<Resource<UserDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getCurrentUser()
            if (response.isSuccessful && response.body() != null) {
                emit(Resource.Success(response.body()!!))
            } else {
                emit(Resource.Error("Không thể lấy thông tin người dùng"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }

    fun updateProfile(request: UpdateProfileRequestDTO): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateProfile(request)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                tokenManager.updateName(request.fullName)
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(body?.message ?: "Cập nhật hồ sơ thất bại"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi không xác định"))
        }
    }
}
