package com.example.healthcaremonitoringapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcaremonitoringapp.databinding.ActivityLoginBinding
import com.example.healthcaremonitoringapp.ui.patient.DashboardActivity
import com.example.healthcaremonitoringapp.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validasi input
            when {
                email.isEmpty() && password.isEmpty() -> {
                    Toast.makeText(this, "Email dan password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    Toast.makeText(this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    Toast.makeText(this, "Format email salah!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                else -> {
                    // Kirim data ke ViewModel untuk proses login
                    authViewModel.login(email, password)
                }
            }
        }

        observeLoginState()
    }

    private fun observeLoginState() {
        lifecycleScope.launch {
            authViewModel.loginState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.loginButton.isEnabled = false
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        showLoginSuccessDialog()
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        handleLoginError(state.message)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun handleLoginError(message: String) {
        when {
            message.contains("Email tidak ditemukan", true) -> {
                Toast.makeText(this, "Email tidak tersedia, silakan periksa kembali!", Toast.LENGTH_SHORT).show()
            }
            message.contains("Password salah", true) -> {
                Toast.makeText(this, "Password salah, silakan coba lagi!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Berhasil")
            .setMessage("Selamat datang! Anda berhasil masuk ke aplikasi.")
            .setPositiveButton("Lanjutkan") { dialog, _ ->
                startActivity(Intent(this, DashboardActivity::class.java))
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}
