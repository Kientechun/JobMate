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
    val location: String,
    val salary: Double,
    val description: String,
    val type: String,
    val experience: String,
    val postedDate: String
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

// Mappers
fun UserDTO.toDomain() = User(id, email, fullName, roles)
fun JobDTO.toDomain() = Job(id, title, companyName, location, salaryRange, description, jobType, experience, createdAt)
fun CategoryDTO.toDomain() = Category(id, name)
fun AuthResponseDTO.toDomain() = AuthResponse(token, refreshToken, User(id, email, fullName, roles))
