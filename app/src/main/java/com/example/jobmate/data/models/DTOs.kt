package com.example.jobmate.data.models

import com.google.gson.annotations.SerializedName

/**
 * DTOs (Data Transfer Objects): Sử dụng để map trực tiếp với JSON từ API.
 */

data class ApiResponseDTO<T>(
    @SerializedName("succeeded") val succeeded: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?,
    @SerializedName("errors") val errors: List<String>?
)

data class PaginatedListDTO<T>(
    @SerializedName("items") val items: List<T>,
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("hasPreviousPage") val hasPreviousPage: Boolean,
    @SerializedName("hasNextPage") val hasNextPage: Boolean
)

data class LoginRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDTO(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("role") val role: String // "Candidate", "Recruiter", "Admin"
)

data class RefreshRequestDTO(
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class AuthResponseDTO(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("token") val token: String,
    @SerializedName("refreshToken") val refreshToken: String,
    @SerializedName("roles") val roles: List<String>
)

data class UserDTO(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("roles") val roles: List<String>
)

data class JobDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("location") val location: String,
    @SerializedName("salaryRange") val salaryRange: Double,
    @SerializedName("jobType") val jobType: String,
    @SerializedName("experience") val experience: String,
    @SerializedName("expiryDate") val expiryDate: String,
    @SerializedName("status") val status: Int,
    @SerializedName("companyId") val companyId: String,
    @SerializedName("createdAt") val createdAt: String
)

data class CategoryDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)

data class ResumeDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("address") val address: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("isDefault") val isDefault: Boolean,
    @SerializedName("fileUrl") val fileUrl: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("experiences") val experiences: List<ExperienceDTO>?,
    @SerializedName("educations") val educations: List<EducationDTO>?,
    @SerializedName("skills") val skills: List<SkillDTO>?,
    @SerializedName("projects") val projects: List<ProjectDTO>?,
    @SerializedName("certificates") val certificates: List<CertificateDTO>?,
    @SerializedName("languages") val languages: List<LanguageDTO>?
)

data class ExperienceDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("position") val position: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("description") val description: String
)

data class EducationDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("schoolName") val schoolName: String,
    @SerializedName("degree") val degree: String,
    @SerializedName("fieldOfStudy") val fieldOfStudy: String,
    @SerializedName("startDate") val startDate: String,
    @SerializedName("endDate") val endDate: String,
    @SerializedName("description") val description: String
)

data class SkillDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("skillName") val skillName: String,
    @SerializedName("level") val level: String
)

data class ProjectDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("projectName") val projectName: String,
    @SerializedName("description") val description: String,
    @SerializedName("link") val link: String
)

data class CertificateDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("certificateName") val certificateName: String,
    @SerializedName("issuingOrganization") val issuingOrganization: String,
    @SerializedName("issueDate") val issueDate: String,
    @SerializedName("expiryDate") val expiryDate: String
)

data class LanguageDTO(
    @SerializedName("id") val id: Int?,
    @SerializedName("language") val language: String,
    @SerializedName("level") val level: String
)

data class CreateResumeRequestDTO(
    @SerializedName("title") val title: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("address") val address: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("experiences") val experiences: List<ExperienceDTO>,
    @SerializedName("educations") val educations: List<EducationDTO>,
    @SerializedName("skills") val skills: List<SkillDTO>,
    @SerializedName("projects") val projects: List<ProjectDTO>,
    @SerializedName("certificates") val certificates: List<CertificateDTO>,
    @SerializedName("languages") val languages: List<LanguageDTO>
)

data class ApplicationDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("jobId") val jobId: Int,
    @SerializedName("job") val job: JobDTO?,
    @SerializedName("resumeId") val resumeId: Int?,
    @SerializedName("candidateName") val candidateName: String,
    @SerializedName("candidateEmail") val candidateEmail: String,
    @SerializedName("resumeUrl") val resumeUrl: String?,
    @SerializedName("coverLetter") val coverLetter: String?,
    @SerializedName("status") val status: Int, // Enums match ApplicationStatus
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("histories") val histories: List<ApplicationHistoryDTO>?
)

data class ApplicationHistoryDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("note") val note: String,
    @SerializedName("createdAt") val createdAt: String
)

data class CreateApplicationRequestDTO(
    @SerializedName("jobId") val jobId: Int,
    @SerializedName("resumeId") val resumeId: Int,
    @SerializedName("coverLetter") val coverLetter: String
)

// AI DTOs
data class ResumeAnalysisDTO(
    @SerializedName("overview") val overview: String,
    @SerializedName("strengths") val strengths: List<String>,
    @SerializedName("improvements") val improvements: List<String>,
    @SerializedName("highlightSkills") val highlightSkills: List<String>,
    @SerializedName("missingSkills") val missingSkills: List<String>,
    @SerializedName("suggestions") val suggestions: List<String>,
    @SerializedName("suitableRoles") val suitableRoles: List<String>
)

data class JobRecommendationDTO(
    @SerializedName("jobId") val jobId: Int,
    @SerializedName("jobTitle") val jobTitle: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("matchScore") val matchScore: Double,
    @SerializedName("reason") val reason: String
)
