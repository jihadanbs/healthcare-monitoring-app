package com.example.healthcaremonitoringapp.ui.patient.checkout

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.CheckoutItem
import com.example.healthcaremonitoringapp.models.Doctor
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.network.DashboardPatientRepository
import com.example.healthcaremonitoringapp.network.PatientApiService
import com.example.healthcaremonitoringapp.network.RetrofitClient
import com.example.healthcaremonitoringapp.ui.patient.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var checkoutButton: Button
    private lateinit var contentLayout: View

    private var isDataProcessed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Disable back navigation to dashboard
        if (intent.hasExtra("SELECTED_MEDICINE")) {
            // This prevents the activity from being added to the back stack
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Inisialisasi ViewModel dengan repository
        val apiService = RetrofitClient.create(PatientApiService::class.java)
        val repository = DashboardPatientRepository(apiService)
        viewModel = ViewModelProvider(
            this,
            CheckoutViewModelFactory(repository)
        )[CheckoutViewModel::class.java]

        inisialisasiView()
        setupViewModel()
        setupRecyclerView()
        amatiPerubahanData()
        setupTombolCheckout()
        tanganiDataMasuk()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.navigation_checkout -> true
                R.id.navigation_account -> {
                    true
                }

                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_checkout
    }

    // Override onBackPressed to handle back navigation
    override fun onBackPressed() {
        // Show confirmation dialog before leaving checkout
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Keluar dari Checkout")
            .setMessage("Apakah Anda yakin ingin keluar dari halaman checkout?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                super.onBackPressed()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInProgressMedicines()
    }

    private fun inisialisasiView() {
        recyclerView = findViewById(R.id.checkoutRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        checkoutButton = findViewById(R.id.checkoutButton)
        contentLayout = findViewById(R.id.contentLayout)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(CheckoutViewModel::class.java)
    }

    private fun tanganiDataMasuk() {
        try {
            val medicine = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("SELECTED_MEDICINE", Medicine::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("SELECTED_MEDICINE")
            }

            Log.d("CheckoutActivity", "Data obat dari intent: $medicine")

            medicine?.let { selectedMedicine ->
                // Ambil daftar CheckoutItem saat ini
                val currentList = viewModel.checkoutItems.value.orEmpty().toMutableList()

                // Cari jika sudah ada CheckoutItem yang sesuai
                val existingItem = currentList.find { it.medicines.contains(selectedMedicine) }

                if (existingItem != null) {
                    // Tambahkan Medicine ke item yang sudah ada (hindari duplikasi)
                    if (!existingItem.medicines.contains(selectedMedicine)) {
                        val updatedMedicines = existingItem.medicines.toMutableList()
                        updatedMedicines.add(selectedMedicine)

                        val updatedItem = existingItem.copy(medicines = updatedMedicines)
                        currentList[currentList.indexOf(existingItem)] = updatedItem
                    }
                } else {
                    // Buat CheckoutItem baru jika belum ada
                    val newItem = CheckoutItem(
                        recordId = generateUniqueId(),
                        consultationDate = getCurrentDate(), // Implementasikan fungsi sesuai kebutuhan
                        doctor = getCurrentDoctor(),        // Ambil data dokter yang sesuai
                        medicines = listOf(selectedMedicine),
                        totalAmount = calculateTotalAmount(selectedMedicine) // Implementasikan fungsi sesuai kebutuhan
                    )
                    currentList.add(newItem)
                }

                val medicinesList = currentList.flatMap { it.medicines }
                viewModel.setCheckoutItems(medicinesList)
            }
        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error saat menangani data masuk", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateUniqueId(): String {
        return System.currentTimeMillis().toString()
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(Date())
    }

    private fun getCurrentDoctor(): Doctor {
        // Implementasikan logika untuk mendapatkan data dokter saat ini
        return Doctor("1", "Dr. Example")
    }

    private fun calculateTotalAmount(medicine: Medicine): Int {
        // Hitung total berdasarkan medicine atau logika lain
        return medicine.price
    }


    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutAdapter { obat ->
            viewModel.removeFromCheckout(obat.id)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CheckoutActivity)
            adapter = checkoutAdapter
            addItemDecoration(DividerItemDecoration(this@CheckoutActivity, DividerItemDecoration.VERTICAL))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("CHECKOUT_ITEMS", ArrayList(viewModel.selectedMedicines.value.orEmpty()))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val items = savedInstanceState.getParcelableArrayList<Medicine>("CHECKOUT_ITEMS")
        items?.let { viewModel.setCheckoutItems(it) }
    }


    private fun setupTombolCheckout() {
        checkoutButton.setOnClickListener {
            if (!viewModel.validateCheckout()) {
                Toast.makeText(this, "Keranjang belanja masih kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan dialog konfirmasi
            showConfirmationDialog()
        }
    }

    private fun showConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Konfirmasi Pembayaran")
            .setMessage("Apakah Anda yakin ingin melakukan pembayaran?")
            .setPositiveButton("Ya") { dialog, _ ->
                dialog.dismiss()
                processCheckout()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun processCheckout() {
        // Disable checkout button sementara proses berlangsung
        checkoutButton.isEnabled = false

        // Tampilkan loading
        tampilkanLoading()

        // Proses checkout melalui ViewModel
        viewModel.processCheckout() // Add parameter to indicate actual checkout
    }

    private fun amatiPerubahanData() {
        viewModel.selectedMedicines.observe(this) { daftarObat ->
            if (!isDataProcessed) {
                Log.d("CheckoutActivity", "Menerima daftar obat: $daftarObat")
                checkoutAdapter.submitList(daftarObat)
                updateUIState(daftarObat.isNullOrEmpty())
                isDataProcessed = true
            }
        }

        viewModel.totalPrice.observe(this) { total ->
            totalPriceTextView.text = getString(R.string.total_price_format, total)
            totalPriceTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
        }

        viewModel.checkoutState.observe(this) { state ->
            when (state) {
                is CheckoutViewModel.CheckoutState.Loading -> tampilkanLoading()
                is CheckoutViewModel.CheckoutState.Success -> {
                    sembunyikanLoading()
                    if (state.isCheckoutProcess) {
                        handleCheckoutSuccess()
                    }
                }
                is CheckoutViewModel.CheckoutState.Error -> handleCheckoutError(state.message)
                is CheckoutViewModel.CheckoutState.Initial -> sembunyikanLoading()
            }
        }

        viewModel.error.observe(this) { pesanError ->
            pesanError?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }

    private fun handleCheckoutSuccess() {
        sembunyikanLoading()

        // Tampilkan pesan sukses
        val successToast = Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_LONG)
        successToast.show()

        // Delay sebelum kembali ke dashboard
        Handler(Looper.getMainLooper()).postDelayed({
            setResult(RESULT_OK)
            finish()
        }, 1500) // Delay 1.5 detik
    }

    private fun handleCheckoutError(message: String) {
        sembunyikanLoading()
        showError("Pembayaran gagal: $message")
        checkoutButton.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun updateUIState(isEmpty: Boolean) {
        if (isEmpty) {
            // Tampilkan pesan keranjang kosong
            recyclerView.isVisible = false
            checkoutButton.isEnabled = false
            totalPriceTextView.text = "Total: Rp 0"
        } else {
            recyclerView.isVisible = true
            checkoutButton.isEnabled = true
        }
    }

    private fun tampilkanLoading() {
        loadingProgressBar.isVisible = true
        contentLayout.animate()
            .alpha(0.5f)
            .setDuration(300)
            .start()
        checkoutButton.isEnabled = false
    }

    private fun sembunyikanLoading() {
        loadingProgressBar.animate()
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                loadingProgressBar.isVisible = false
            }
            .start()

        contentLayout.animate()
            .alpha(1.0f)
            .setDuration(300)
            .start()
        checkoutButton.isEnabled = true
    }
}

class CheckoutViewModelFactory(
    private val repository: DashboardPatientRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}