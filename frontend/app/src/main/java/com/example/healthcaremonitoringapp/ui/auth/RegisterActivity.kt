package com.example.healthcaremonitoringapp.ui.auth

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcaremonitoringapp.databinding.ActivityRegisterBinding
import com.example.healthcaremonitoringapp.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            // Validasi input
            when {
                name.isEmpty() && email.isEmpty() && password.isEmpty() -> {
                    Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                name.isEmpty() -> {
                    Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
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
                    // Kirim data ke ViewModel untuk proses registrasi
                    viewModel.register(name, email, password)
                }
            }
        }

        // Observe registration state
        lifecycleScope.launch {
            viewModel.registrationState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.registerButton.isEnabled = false
                    }
                    is AuthViewModel.AuthState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    is AuthViewModel.AuthState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.registerButton.isEnabled = true
                        handleRegistrationError(state.message)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.registerButton.isEnabled = true
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        binding.loginLinkText.setOnClickListener {
            finish()
        }
    }

    private fun handleRegistrationError(message: String) {
        when {
            message.contains("Email sudah terdaftar!", true) -> {
                Toast.makeText(this, "Email sudah terdaftar, silakan gunakan email lain!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
