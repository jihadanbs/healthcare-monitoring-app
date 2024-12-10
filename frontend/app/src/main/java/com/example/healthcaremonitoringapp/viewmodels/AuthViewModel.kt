package com.example.healthcaremonitoringapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.AuthResponse
import com.example.healthcaremonitoringapp.models.LoginRequest
import com.example.healthcaremonitoringapp.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<AuthState>(AuthState.Initial)
    val loginState: StateFlow<AuthState> = _loginState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    _loginState.value = AuthState.Success(authResponse!!)
                } else {
                    _loginState.value = AuthState.Error(response.message())
                }
            } catch (e: Exception) {
                _loginState.value = AuthState.Error(e.message ?: "Login Failed")
            }
        }
    }

    sealed class AuthState {
        object Initial : AuthState()
        object Loading : AuthState()
        data class Success(val authResponse: AuthResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}