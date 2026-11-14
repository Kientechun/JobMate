package com.example.jobmate.data.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val roles: List<String>
)

data class Job(
    val id: Int,
    val title: String,
    val companyName: String,
    val companyLogo: String? = null,
    val location: String,
    val salaryRange: Double,
    val description: String,
    val requirements: String? = null,
    val benefits: String? = null,
    val type: String,
    val experience: String,
    val postedDate: String,
    val isSaved: Boolean = false
)

data class Category(
    val id: Int,
    val name: String,
    val iconRes: Int = 0
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: User
)

enum class ApplicationStatus(val value: Int) {
    PENDING(0),
    APPROVED(1),
    REJECTED(2),
    UNDER_REVIEW(3);

    companion object {
        fun fromInt(value: Int) = values().find { it.value == value } ?: PENDING
    }
}

// Mappers
fun UserDTO.toDomain() = User(id, email, fullName, roles)
fun JobDTO.toDomain() = Job(
    id = id,
    title = title,
    companyName = companyName,
    companyLogo = companyLogo,
    location = location,
    salaryRange = salaryRange,
    description = description,
    requirements = requirements,
    benefits = benefits,
    type = jobType,
    experience = experience,
    postedDate = createdAt,
    isSaved = isSaved
)
fun CategoryDTO.toDomain() = Category(id, name)
fun AuthResponseDTO.toDomain() = AuthResponse(token, refreshToken, User(id, email, fullName, roles))
