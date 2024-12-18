package com.example.healthcaremonitoringapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcaremonitoringapp.ui.patient.DashboardActivity
import com.example.healthcaremonitoringapp.databinding.ActivityLoginBinding
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
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Validasi input
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authViewModel.login(email, password)
        }

        lifecycleScope.launch {
            authViewModel.loginState.collect { state ->
                when (state) {
                    is AuthViewModel.AuthState.Loading -> {
                        // Tampilan Loading
                        binding.progressBar.visibility = View.VISIBLE
                        binding.loginButton.isEnabled = false
                    }
                    is AuthViewModel.AuthState.Success -> {
                        // Sembunyikan progress bar
                        binding.progressBar.visibility = View.GONE

                        // Tampilkan dialog login sukses
                        showLoginSuccessDialog()
                    }
                    is AuthViewModel.AuthState.Error -> {
                        // Sembunyikan progress bar
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true

                        // Tampilkan pesan error
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loginButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Login Berhasil")
            .setMessage("Selamat datang! Anda berhasil masuk ke aplikasi.")
            .setPositiveButton("Lanjutkan") { dialog, _ ->
                // Navigasi ke halaman Dashboard
                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}