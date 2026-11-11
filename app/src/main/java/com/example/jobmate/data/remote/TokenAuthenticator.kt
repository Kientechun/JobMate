package com.example.jobmate.data.remote

import com.example.jobmate.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val apiService: dagger.Lazy<ApiService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Tránh vòng lặp vô tận nếu request refresh cũng trả về 401
        if (response.request.url.encodedPath.contains("/api/v1/Auth/refresh")) {
            runBlocking { tokenManager.clear() }
            return null
        }

        val refreshToken = runBlocking { tokenManager.refreshToken.first() }
        val accessToken = runBlocking { tokenManager.accessToken.first() }

        if (refreshToken == null || accessToken == null) {
            return null
        }

        synchronized(this) {
            val currentAccessToken = runBlocking { tokenManager.accessToken.first() }
            val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")
            
            if (currentAccessToken != requestToken && currentAccessToken != null) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            val newAccessToken = runBlocking {
                try {
                    val refreshRequest = com.example.jobmate.data.models.RefreshRequestDTO(accessToken, refreshToken)
                    val refreshResponse = apiService.get().refreshToken(refreshRequest)
                    if (refreshResponse.isSuccessful && refreshResponse.body()?.succeeded == true) {
                        val authResponse = refreshResponse.body()!!.data!!
                        tokenManager.saveTokens(
                            authResponse.token,
                            authResponse.refreshToken,
                            authResponse.roles.firstOrNull() ?: "Candidate",
                            authResponse.email,
                            authResponse.fullName
                        )
                        authResponse.token
                    } else {
                        tokenManager.clear()
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            return if (newAccessToken != null) {
                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } else {
                null
            }
        }
    }
}
