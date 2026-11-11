package com.example.jobmate.di

import com.example.jobmate.data.local.TokenManager
import com.example.jobmate.data.remote.ApiService
import com.example.jobmate.data.remote.AuthInterceptor
import com.example.jobmate.data.remote.MockInterceptor
import com.example.jobmate.data.remote.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        mockInterceptor: MockInterceptor,
        tokenAuthenticator: TokenAuthenticator,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(mockInterceptor) // Kích hoạt Mock Data khi chưa có Backend
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(client: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/") // Địa chỉ mặc định cho Emulator truy cập host
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
