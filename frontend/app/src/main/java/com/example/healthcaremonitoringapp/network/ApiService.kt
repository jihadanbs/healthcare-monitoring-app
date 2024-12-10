package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.AuthResponse
import com.example.healthcaremonitoringapp.models.LoginRequest
import com.example.healthcaremonitoringapp.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>
}