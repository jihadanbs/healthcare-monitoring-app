package com.example.healthcaremonitoringapp.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String
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

data class AuthResponse(
    val token: String,
    val user: User,
    val message: String
)