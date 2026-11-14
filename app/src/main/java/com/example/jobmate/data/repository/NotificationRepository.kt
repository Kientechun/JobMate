package com.example.jobmate.data.repository

import com.example.jobmate.data.models.DeviceTokenRequestDTO
import com.example.jobmate.data.models.NotificationDTO
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: ApiService
) {
    fun getNotifications(): Flow<Resource<List<NotificationDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getNotifications()
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(body.data ?: emptyList()))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi lấy danh sách thông báo"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun markAsRead(id: Int): Flow<Resource<Unit>> = flow {
        try {
            val response = apiService.markNotificationAsRead(id)
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi cập nhật trạng thái thông báo"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }

    fun registerDeviceToken(token: String): Flow<Resource<Unit>> = flow {
        try {
            val response = apiService.registerDeviceToken(DeviceTokenRequestDTO(token, "Android"))
            val body = response.body()
            if (response.isSuccessful && body?.succeeded == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(body?.message ?: "Lỗi đăng ký token thiết bị"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Lỗi kết nối"))
        }
    }
}
