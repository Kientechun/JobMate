package com.example.jobmate.data.repository

import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.data.models.AuthResponse
import com.example.jobmate.data.models.AuthResponseDTO
import com.example.jobmate.data.models.LoginRequestDTO
import com.example.jobmate.data.models.RegisterRequestDTO
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

    suspend fun logout() {
        tokenManager.clear()
    }
    
    fun getUserRole() = tokenManager.userRole
}
