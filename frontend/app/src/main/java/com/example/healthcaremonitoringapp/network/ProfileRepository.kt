package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.UpdateProfileRequest

class ProfileRepository(private val apiService: AuthApiService) {

    suspend fun getProfile() = apiService.getProfile()

    suspend fun updateProfile(updateProfileRequest: UpdateProfileRequest) =
        apiService.updateProfile(updateProfileRequest)

    suspend fun deleteProfile() = apiService.deleteProfile()
}
