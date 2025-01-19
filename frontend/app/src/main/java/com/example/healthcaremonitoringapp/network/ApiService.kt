package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.AuthResponse
import com.example.healthcaremonitoringapp.models.LoginRequest
import com.example.healthcaremonitoringapp.models.MessageResponse
import com.example.healthcaremonitoringapp.models.ProfileResponse
import com.example.healthcaremonitoringapp.models.RegisterRequest
import com.example.healthcaremonitoringapp.models.UpdateProfileRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<AuthResponse>

    @GET("auth/profile")
    suspend fun getProfile(): Response<ProfileResponse>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body updateProfileRequest: UpdateProfileRequest): Response<AuthResponse>

    @DELETE("auth/profile")
    suspend fun deleteProfile(): Response<MessageResponse>
}