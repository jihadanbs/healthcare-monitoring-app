package com.example.healthcaremonitoringapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.*
import com.example.healthcaremonitoringapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _updateProfileState = MutableStateFlow<ProfileState>(ProfileState.Initial)
    val updateProfileState: StateFlow<ProfileState> = _updateProfileState

    private val _deleteProfileState = MutableStateFlow<DeleteState>(DeleteState.Initial)
    val deleteProfileState: StateFlow<DeleteState> = _deleteProfileState

    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val response = RetrofitClient.instance.getProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profileResponse = response.body()!!
                    // Map response to User model
                    val user = User(
                        id = profileResponse._id,
                        name = profileResponse.name,
                        email = profileResponse.email,
                        password = "",
                        role = profileResponse.role,
                        profile = UserProfile(
                            age = profileResponse.profile?.age,
                            gender = profileResponse.profile?.gender,
                            phoneNumber = profileResponse.profile?.phoneNumber
                        )
                    )
                    // Di ProfileViewModel
                    Log.d("ProfileViewModel", "Raw response: ${response.body()}")

                    _profileState.value = ProfileState.Success(AuthResponse(
                        token = "",
                        user = user,
                        message = "Profile retrieved successfully"
                    ))
                } else {
                    _profileState.value = ProfileState.Error(
                        response.errorBody()?.string() ?: "Failed to load profile"
                    )
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error(
                    e.message ?: "Failed to load profile"
                )
            }
        }
    }

    fun updateProfile(name: String, email: String, password: String?, profile: UserProfile) {
        viewModelScope.launch {
            _updateProfileState.value = ProfileState.Loading
            try {
                val updateRequest = UpdateProfileRequest(
                    name = name,
                    email = email,
                    password = password?.takeIf { it.isNotBlank() },
                    profile = profile
                )
                val response = RetrofitClient.instance.updateProfile(updateRequest)
                if (response.isSuccessful) {
                    _updateProfileState.value = ProfileState.Success(response.body()!!)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: response.message()
                    _updateProfileState.value = ProfileState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _updateProfileState.value = ProfileState.Error(e.message ?: "Gagal memperbarui profile")
            }
        }
    }

    fun deleteProfile() {
        viewModelScope.launch {
            _deleteProfileState.value = DeleteState.Loading
            try {
                val response = RetrofitClient.instance.deleteProfile()
                if (response.isSuccessful) {
                    _deleteProfileState.value = DeleteState.Success(response.body()?.message ?: "Akun berhasil dihapus")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: response.message()
                    _deleteProfileState.value = DeleteState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _deleteProfileState.value = DeleteState.Error(e.message ?: "Gagal menghapus akun")
            }
        }
    }

    sealed class ProfileState {
        object Initial : ProfileState()
        object Loading : ProfileState()
        data class Success(val authResponse: AuthResponse) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class DeleteState {
        object Initial : DeleteState()
        object Loading : DeleteState()
        data class Success(val message: String) : DeleteState()
        data class Error(val message: String) : DeleteState()
    }
}