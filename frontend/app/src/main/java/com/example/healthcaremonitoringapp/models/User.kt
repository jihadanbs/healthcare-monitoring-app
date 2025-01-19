package com.example.healthcaremonitoringapp.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val password: String?,
    val profile: UserProfile?
)

data class UserProfile(
    val age: Int?,
    val gender: String?,
    val phoneNumber: String?,
    val specialization: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val password: String?,
    val profile: UserProfile
)

data class AuthResponse(
    val token: String?,
    val user: User,
    val message: String?,
)

data class MessageResponse(
    val message: String
)

data class ProfileResponse(
    val _id: String,
    val name: String,
    val email: String,
    val role: String,
    val password: String?,
    val profile: ProfileData?,
    val verified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class ProfileData(
    val age: Int?,
    val gender: String?,
    val phoneNumber: String?
)
