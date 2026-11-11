package com.example.jobmate.data.remote

import com.example.jobmate.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        
        // Tránh gắn token vào các API public
        if (path.contains("/api/v1/Auth/login") || 
            path.contains("/api/v1/Auth/register") || 
            path.contains("/api/v1/Auth/refresh")) {
            return chain.proceed(request)
        }

        val token = runBlocking { tokenManager.accessToken.first() }
        val newRequest = request.newBuilder()
        
        token?.let {
            newRequest.addHeader("Authorization", "Bearer $it")
        }
        
        return chain.proceed(newRequest.build())
    }
}
