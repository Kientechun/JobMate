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

    @POST("api/v1/Auth/logout")
    suspend fun logout(): Response<ApiResponseDTO<Boolean>>

    @GET("api/v1/Auth/me")
    suspend fun getCurrentUser(): Response<UserDTO>

    @PUT("api/v1/Auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDTO): Response<ApiResponseDTO<Boolean>>

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
    suspend fun analyzeResume(@Path("id") id: Int): Response<ApiResponseDTO<ResumeAnalysisDTO>>

    @GET("api/v1/ai/resumes/{id}/analysis")
    suspend fun getResumeAnalysis(@Path("id") id: Int): Response<ApiResponseDTO<ResumeAnalysisDTO>>

    @GET("api/v1/ai/jobs/recommendations")
    suspend fun getJobRecommendations(@Query("resumeId") resumeId: Int? = null): Response<ApiResponseDTO<List<JobRecommendationDTO>>>

    // Notification API
    @GET("api/v1/Notifications")
    suspend fun getNotifications(): Response<ApiResponseDTO<List<NotificationDTO>>>

    @PATCH("api/v1/Notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: Int): Response<ApiResponseDTO<Unit>>

    @PATCH("api/v1/Notifications/read-all")
    suspend fun markAllNotificationsAsRead(): Response<ApiResponseDTO<Unit>>

    @DELETE("api/v1/Notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Int): Response<ApiResponseDTO<Unit>>

    @POST("api/v1/Notifications/tokens")
    suspend fun registerDeviceToken(@Body request: DeviceTokenRequestDTO): Response<ApiResponseDTO<Unit>>

    @DELETE("api/v1/Notifications/tokens/{token}")
    suspend fun unregisterDeviceToken(@Path("token") string: String): Response<ApiResponseDTO<Unit>>

    // Interview API
    @GET("api/v1/Interviews")
    suspend fun getMyInterviews(): Response<ApiResponseDTO<List<InterviewDTO>>>

    @GET("api/v1/Interviews/{id}")
    suspend fun getInterviewDetail(@Path("id") id: Int): Response<ApiResponseDTO<InterviewDTO>>

    // Recruiter API
    @GET("api/v1/Recruiter/dashboard")
    suspend fun getRecruiterDashboard(): Response<ApiResponseDTO<RecruiterDashboardDTO>>

    @GET("api/v1/Recruiter/jobs")
    suspend fun getRecruiterJobs(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponseDTO<PaginatedListDTO<JobDTO>>>

    @POST("api/v1/Recruiter/jobs")
    suspend fun createJob(@Body request: CreateJobRequestDTO): Response<ApiResponseDTO<JobDTO>>

    @PUT("api/v1/Recruiter/jobs/{id}")
    suspend fun updateJob(@Path("id") id: Int, @Body request: CreateJobRequestDTO): Response<ApiResponseDTO<JobDTO>>

    @PATCH("api/v1/Recruiter/jobs/{id}/close")
    suspend fun closeJob(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    @GET("api/v1/Recruiter/applications")
    suspend fun getRecruiterApplications(
        @Query("jobId") jobId: Int?,
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponseDTO<PaginatedListDTO<ApplicationDTO>>>

    @PATCH("api/v1/Recruiter/applications/{id}/status")
    suspend fun updateApplicationStatus(
        @Path("id") id: Int,
        @Body request: UpdateApplicationStatusRequestDTO
    ): Response<ApiResponseDTO<Boolean>>

    // Admin API
    @GET("api/v1/Admin/dashboard")
    suspend fun getAdminDashboard(): Response<ApiResponseDTO<AdminDashboardDTO>>

    @GET("api/v1/Admin/users")
    suspend fun getUsers(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponseDTO<PaginatedListDTO<UserManagementDTO>>>

    @PATCH("api/v1/Admin/users/{id}/lock")
    suspend fun lockUser(@Path("id") id: String): Response<ApiResponseDTO<Boolean>>

    @PATCH("api/v1/Admin/users/{id}/unlock")
    suspend fun unlockUser(@Path("id") id: String): Response<ApiResponseDTO<Boolean>>

    @GET("api/v1/Admin/jobs/pending")
    suspend fun getPendingJobs(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int
    ): Response<ApiResponseDTO<PaginatedListDTO<JobDTO>>>

    @PATCH("api/v1/Admin/jobs/{id}/approve")
    suspend fun approveJob(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    @PATCH("api/v1/Admin/jobs/{id}/reject")
    suspend fun rejectJob(@Path("id") id: Int, @Body request: RejectJobRequestDTO): Response<ApiResponseDTO<Boolean>>

    @POST("api/v1/Admin/categories")
    suspend fun createCategory(@Body request: CategoryRequestDTO): Response<ApiResponseDTO<CategoryDTO>>

    @PUT("api/v1/Admin/categories/{id}")
    suspend fun updateCategory(@Path("id") id: Int, @Body request: CategoryRequestDTO): Response<ApiResponseDTO<CategoryDTO>>

    @DELETE("api/v1/Admin/categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<ApiResponseDTO<Boolean>>

    @POST("api/v1/Resumes/upload")
    @Multipart
    suspend fun uploadResume(@Part file: okhttp3.MultipartBody.Part): Response<ApiResponseDTO<String>>
}
