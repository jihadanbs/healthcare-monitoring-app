package com.example.healthcaremonitoringapp.ui.auth

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.databinding.ActivityProfileBinding
import com.example.healthcaremonitoringapp.models.UserProfile
import com.example.healthcaremonitoringapp.network.RetrofitClient
import com.example.healthcaremonitoringapp.ui.patient.DashboardActivity
import com.example.healthcaremonitoringapp.ui.patient.checkout.CheckoutActivity
import com.example.healthcaremonitoringapp.utils.TokenManager
import com.example.healthcaremonitoringapp.viewmodels.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        loadProfile()
        setupButtons()
        setupSocialMediaLinks()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Mengatur warna teks item menu
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked), // Status item yang dipilih
                intArrayOf(-android.R.attr.state_checked) // Status item yang tidak dipilih
            ),
            intArrayOf(
                resources.getColor(R.color.ungu, theme), // Warna untuk item yang dipilih
                resources.getColor(R.color.blue, theme)  // Warna untuk item yang tidak dipilih
            )
        )
        bottomNavigation.itemTextColor = colorStateList
        bottomNavigation.itemIconTintList = colorStateList

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.navigation_checkout -> {
                    startActivity(Intent(this, CheckoutActivity::class.java))
                    true
                }
                R.id.navigation_account -> true

                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_account
    }

    private fun setupSocialMediaLinks() {
        binding.apply {
            // WhatsApp
            ivWhatsApp.setOnClickListener {
                openWhatsApp("6288215178312")
            }

            // Instagram
            ivInstagram.setOnClickListener {
                openInstagram("jihadanbs")
            }

            // GitHub
            ivGithub.setOnClickListener {
                openUrl("https://github.com/jihadanbs")
            }
        }
    }

    private fun openWhatsApp(number: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=$number"
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInstagram(username: String) {
        try {
            // Mencoba membuka di aplikasi Instagram
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://instagram.com/_u/$username")
            intent.setPackage("com.instagram.android")
            startActivity(intent)
        } catch (e: Exception) {
            // Jika aplikasi Instagram tidak terinstall, buka di browser
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("http://instagram.com/$username")
            startActivity(intent)
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Anda yakin ingin keluar?")
            .setPositiveButton("Ya") { _, _ ->
                logout()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun logout() {
        // Clear token
        RetrofitClient.setAuthToken("")

        // Navigate to Login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun loadProfile() {
        viewModel.getProfile()
    }

    private fun setupObservers() {
        // Observer untuk Profile State
        lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is ProfileViewModel.ProfileState.Success -> {
                        hideLoading()
                        val user = state.authResponse.user

                        // Debug log
                        Log.d("ProfileActivity", "Received user data: $user")
                        Log.d("ProfileActivity", "Current state: $state")

                        binding.apply {
                            etName.setText(user.name)
                            etEmail.setText(user.email)
                            etPassword.setText(user.password)
                            user.profile?.let { profile ->
                                etAge.setText(profile.age?.toString() ?: "")
                                etPhone.setText(profile.phoneNumber ?: "")

                                // Handle gender spinner
                                val genderOptions = listOf("Pilih Jenis Kelamin", "Laki-laki", "Perempuan")
                                val adapter = object : ArrayAdapter<String>(
                                    this@ProfileActivity,
                                    android.R.layout.simple_spinner_item,
                                    genderOptions
                                ) {
                                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                        val view = super.getView(position, convertView, parent) as TextView
                                        view.setTextColor(if (position == 0) Color.GRAY else Color.BLACK)
                                        return view
                                    }

                                    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                        val view = super.getDropDownView(position, convertView, parent) as TextView
                                        view.setTextColor(Color.WHITE)
                                        return view
                                    }
                                }

                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spGender.adapter = adapter

                                // Handle gender spinner
                                val genderPosition = when (profile.gender) {
                                    "Laki-laki" -> 1
                                    "Perempuan" -> 2
                                    else -> 0
                                }
                                spGender.setSelection(genderPosition)
                            }
                        }
                    }

                    is ProfileViewModel.ProfileState.Error -> {
                        hideLoading()
                        Log.e("ProfileActivity", "Error: ${state.message}")
                        Toast.makeText(
                            this@ProfileActivity,
                            "Error: ${state.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is ProfileViewModel.ProfileState.Loading -> showLoading()
                    is ProfileViewModel.ProfileState.Initial -> { /* Do nothing */
                    }
                }
            }
        }

        // Observer untuk Update Profile State
        lifecycleScope.launch {
            viewModel.updateProfileState.collect { state ->
                when (state) {
                    is ProfileViewModel.ProfileState.Loading -> showLoading()
                    is ProfileViewModel.ProfileState.Success -> {
                        hideLoading()
                        binding.etPassword.text?.clear()
                        Toast.makeText(this@ProfileActivity, "Profile berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        loadProfile() // Reload profile data
                    }
                    is ProfileViewModel.ProfileState.Error -> {
                        hideLoading()
                        Toast.makeText(this@ProfileActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is ProfileViewModel.ProfileState.Initial -> { /* Do nothing */ }
                }
            }
        }

        // Observer untuk Delete Profile State
        lifecycleScope.launch {
            viewModel.deleteProfileState.collect { state ->
                when (state) {
                    is ProfileViewModel.DeleteState.Loading -> showLoading()
                    is ProfileViewModel.DeleteState.Success -> {
                        hideLoading()
                        Toast.makeText(this@ProfileActivity, state.message, Toast.LENGTH_SHORT).show()
                        // Kembali ke Login Activity
                        startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
                        finishAffinity()
                    }
                    is ProfileViewModel.DeleteState.Error -> {
                        hideLoading()
                        Toast.makeText(this@ProfileActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is ProfileViewModel.DeleteState.Initial -> { /* Do nothing */ }
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnUpdate.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val age = binding.etAge.text.toString().toIntOrNull()
            val phone = binding.etPhone.text.toString()
            val gender = binding.spGender.selectedItem.toString()

            if (validateInput(name, email)) {
                val profile = UserProfile(
                    age = age,
                    gender = if (gender == "Pilih Jenis Kelamin") null else gender,
                    phoneNumber = phone
                )
                viewModel.updateProfile(name, email, password, profile)
            }
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.btnLogout.setOnClickListener {
            showLogoutConfirmation()
        }
    }

    private fun validateInput(name: String, email: String): Boolean {
        if (name.isBlank() || email.isBlank()) {
            Toast.makeText(this, "Nama dan email harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }
        val password = binding.etPassword.text.toString()
        if (password.isNotEmpty() && password.length < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Hapus Akun")
            .setMessage("Anda yakin ingin menghapus akun?")
            .setPositiveButton("Ya") { _, _ ->
                viewModel.deleteProfile()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnUpdate.isEnabled = false
        binding.btnDelete.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnUpdate.isEnabled = true
        binding.btnDelete.isEnabled = true
    }
}