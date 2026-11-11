package com.example.jobmate.data.remote

import com.example.jobmate.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val path = chain.request().url.encodedPath
        
        // Chỉ giả lập nếu không phải là API thật (ví dụ host không phải localhost hoặc 10.0.2.2)
        // Hoặc có thể dùng cờ cấu hình. Ở đây ta cho phép gọi API thật nếu host khớp.
        val host = chain.request().url.host
        if (host != "localhost" && host != "10.0.2.2" && !host.startsWith("192.168.")) {
             if (!BuildConfig.DEBUG) {
                return chain.proceed(chain.request())
            }
        } else {
            return chain.proceed(chain.request())
        }
        
        // Nếu vẫn muốn dùng Mock cho các path chưa có API thật
        val responseString = when {
            path.endsWith("/api/auth/login") -> """
                {
                    "success": true,
                    "message": "Đăng nhập thành công",
                    "data": {
                        "accessToken": "fake_access_token",
                        "refreshToken": "fake_refresh_token",
                        "user": { "id": "1", "email": "candidate@jobmate.com", "fullName": "Nguyễn Văn A", "role": "Candidate" }
                    }
                }
            """.trimIndent()

            path.endsWith("/api/auth/register") -> """
                {
                    "success": true,
                    "message": "Đăng ký thành công",
                    "data": { "id": "2", "email": "newuser@jobmate.com", "fullName": "Người Dùng Mới", "role": "Candidate" }
                }
            """.trimIndent()

            path.endsWith("/api/jobs") -> """
                {
                    "success": true,
                    "message": "Thành công",
                    "data": [
                        { "id": 1, "title": "Android Developer", "companyName": "TechCorp", "location": "Hà Nội", "salary": "1500 - 2500$", "description": "Làm việc với Kotlin, Coroutines", "requirements": "2 năm kinh nghiệm", "postedDate": "2023-11-10" },
                        { "id": 2, "title": "Backend Developer", "companyName": "SoftSystem", "location": "TP. HCM", "salary": "2000 - 3000$", "description": "Làm việc với ASP.NET Core", "requirements": "3 năm kinh nghiệm", "postedDate": "2023-11-11" },
                        { "id": 3, "title": "UI/UX Designer", "companyName": "CreativeLab", "location": "Đà Nẵng", "salary": "1000 - 1500$", "description": "Thiết kế Figma", "requirements": "1 năm kinh nghiệm", "postedDate": "2023-11-12" },
                        { "id": 4, "title": "Intern Android", "companyName": "StartupX", "location": "Hà Nội", "salary": "Trình thỏa thuận", "description": "Học việc Kotlin", "requirements": "Sinh viên năm cuối", "postedDate": "2023-11-13" }
                    ]
                }
            """.trimIndent()

            path.contains("/api/jobs/") -> """
                {
                    "success": true,
                    "data": { 
                        "id": 1, 
                        "title": "Android Developer", 
                        "companyName": "TechCorp", 
                        "location": "Hà Nội", 
                        "salary": "1500 - 2500$", 
                        "description": "Mô tả chi tiết: Phát triển ứng dụng JobMate bằng Kotlin và Hilt. Xây dựng các tính năng tìm kiếm, ứng tuyển và quản lý hồ sơ.", 
                        "requirements": "Nắm vững MVVM, Retrofit, Coroutines. Ưu tiên ứng viên biết Jetpack Compose.", 
                        "postedDate": "2023-11-10" 
                    }
                }
            """.trimIndent()

            path.endsWith("/api/applications") && chain.request().method == "GET" -> """
                {
                    "success": true,
                    "data": [
                        { "id": 1, "jobId": 1, "jobTitle": "Android Developer", "companyName": "TechCorp", "candidateId": "1", "status": "Chờ duyệt", "appliedDate": "2023-11-20" },
                        { "id": 2, "jobId": 2, "jobTitle": "Backend Developer", "companyName": "SoftSystem", "candidateId": "1", "status": "Đã duyệt", "appliedDate": "2023-11-18" },
                        { "id": 3, "jobId": 3, "jobTitle": "UI/UX Designer", "companyName": "CreativeLab", "candidateId": "1", "status": "Từ chối", "appliedDate": "2023-11-15" }
                    ]
                }
            """.trimIndent()

            path.endsWith("/api/applications") && chain.request().method == "POST" -> """
                {
                    "success": true,
                    "message": "Ứng tuyển thành công"
                }
            """.trimIndent()

            path.endsWith("/api/resumes") -> """
                {
                    "success": true,
                    "data": [
                        { "id": 1, "userId": "1", "fileUrl": "https://jobmate.com/resumes/cv1.pdf", "name": "CV Chính - Android" },
                        { "id": 2, "userId": "1", "fileUrl": "https://jobmate.com/resumes/cv2.pdf", "name": "CV Thiết kế" }
                    ]
                }
            """.trimIndent()

            else -> """{"success": false, "message": "Endpoint không tồn tại trong Mock"}"""
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .addHeader("content-type", "application/json")
            .body(responseString.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
