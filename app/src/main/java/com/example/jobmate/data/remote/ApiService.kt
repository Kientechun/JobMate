package com.example.jobmate.data.remote

import com.example.jobmate.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/v1/Auth/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<ApiResponseDTO<AuthResponseDTO>>

    @POST("api/v1/Auth/register")
    suspend fun register(@Body request: RegisterRequestDTO): Response<ApiResponseDTO<AuthResponseDTO>>

    @POST("api/v1/Auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequestDTO): Response<ApiResponseDTO<AuthResponseDTO>>

    @GET("api/v1/Auth/me")
    suspend fun getCurrentUser(): Response<ApiResponseDTO<UserDTO>>

    @GET("api/v1/Jobs")
    suspend fun getJobs(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Query("Keywords") keywords: String? = null,
        @Query("Location") location: String? = null,
        @Query("JobType") jobType: String? = null,
        @Query("Experience") experience: String? = null
    ): Response<ApiResponseDTO<PaginatedListDTO<JobDTO>>>

    @GET("api/v1/Jobs/{id}")
    suspend fun getJobDetail(@Path("id") id: Int): Response<ApiResponseDTO<JobDTO>>

    @GET("api/v1/Jobs/featured")
    suspend fun getFeaturedJobs(@Query("count") count: Int = 5): Response<ApiResponseDTO<List<JobDTO>>>

    @GET("api/v1/Jobs/categories")
    suspend fun getCategories(): Response<ApiResponseDTO<List<CategoryDTO>>>

    @GET("api/v1/Jobs/saved")
    suspend fun getSavedJobs(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponseDTO<PaginatedListDTO<JobDTO>>>

    @POST("api/v1/Jobs/{id}/save")
    suspend fun saveJob(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    @DELETE("api/v1/Jobs/{id}/save")
    suspend fun unsaveJob(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    // Resume API
    @GET("api/v1/Resumes")
    suspend fun getMyResumes(): Response<ApiResponseDTO<List<ResumeDTO>>>

    @GET("api/v1/Resumes/{id}")
    suspend fun getResumeDetail(@Path("id") id: Int): Response<ApiResponseDTO<ResumeDTO>>

    @POST("api/v1/Resumes")
    suspend fun createResume(@Body request: CreateResumeRequestDTO): Response<ApiResponseDTO<ResumeDTO>>

    @PUT("api/v1/Resumes/{id}")
    suspend fun updateResume(@Path("id") id: Int, @Body request: CreateResumeRequestDTO): Response<ApiResponseDTO<ResumeDTO>>

    @DELETE("api/v1/Resumes/{id}")
    suspend fun deleteResume(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    @PATCH("api/v1/Resumes/{id}/default")
    suspend fun setDefaultResume(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    // Application API
    @POST("api/v1/Applications")
    suspend fun applyJob(@Body request: CreateApplicationRequestDTO): Response<ApiResponseDTO<ApplicationDTO>>

    @GET("api/v1/Applications/my")
    suspend fun getMyApplications(): Response<ApiResponseDTO<List<ApplicationDTO>>>

    @GET("api/v1/Applications/{id}")
    suspend fun getApplicationDetail(@Path("id") id: Int): Response<ApiResponseDTO<ApplicationDTO>>

    @GET("api/v1/Applications/{id}/history")
    suspend fun getApplicationHistory(@Path("id") id: Int): Response<ApiResponseDTO<List<ApplicationHistoryDTO>>>

    @PATCH("api/v1/Applications/{id}/withdraw")
    suspend fun withdrawApplication(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    // AI API
    @POST("api/v1/ai/resumes/{id}/analyze")
    suspend fun analyzeResume(@Path("id") id: Int): Response<ResumeAnalysisDTO>

    @GET("api/v1/ai/resumes/{id}/analysis")
    suspend fun getResumeAnalysis(@Path("id") id: Int): Response<ResumeAnalysisDTO>

    @GET("api/v1/ai/jobs/recommendations")
    suspend fun getJobRecommendations(@Query("resumeId") resumeId: Int? = null): Response<List<JobRecommendationDTO>>
}
