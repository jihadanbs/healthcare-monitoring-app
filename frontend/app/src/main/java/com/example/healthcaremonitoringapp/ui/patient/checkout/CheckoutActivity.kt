package com.example.healthcaremonitoringapp.ui.patient.checkout

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.ui.patient.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class CheckoutActivity : AppCompatActivity() {
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var checkoutAdapter: CheckoutAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var checkoutButton: Button
    private lateinit var contentLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        // Inisialisasi komponen UI
        inisialisasiView()

        // Setup ViewModel
        setupViewModel()

        // Setup RecyclerView
        setupRecyclerView()

        // Amati perubahan data
        amatiPerubahanData()

        // Setup tombol checkout
        setupTombolCheckout()

        // Tangani data yang masuk setelah setup selesai
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
            val obat = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("SELECTED_MEDICINE", Medicine::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra("SELECTED_MEDICINE")
            }

            Log.d("CheckoutActivity", "Data obat dari intent: $obat")

            obat?.let {
                val currentList = viewModel.checkoutItems.value.orEmpty().toMutableList()
                if (!currentList.contains(it)) { // Hindari duplikasi
                    currentList.add(it)
                    viewModel.setCheckoutItems(currentList) // Method baru di ViewModel
                }
            }
        } catch (e: Exception) {
            Log.e("CheckoutActivity", "Error saat menangani data masuk", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
        outState.putParcelableArrayList("CHECKOUT_ITEMS", ArrayList(viewModel.checkoutItems.value.orEmpty()))
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val items = savedInstanceState.getParcelableArrayList<Medicine>("CHECKOUT_ITEMS")
        items?.let { viewModel.setCheckoutItems(it) }
    }


    private fun setupTombolCheckout() {
        checkoutButton.setOnClickListener {
            if (viewModel.validateCheckout()) {
                viewModel.processCheckout()
            } else {
                Toast.makeText(this, "Keranjang belanja masih kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun amatiPerubahanData() {
        viewModel.checkoutItems.observe(this) { daftarObat ->
            Log.d("CheckoutActivity", "Menerima daftar obat: $daftarObat")
            checkoutAdapter.submitList(daftarObat)
            // Update UI berdasarkan ketersediaan data
            updateUIState(daftarObat.isNullOrEmpty())
        }

        viewModel.totalPrice.observe(this) { total ->
            totalPriceTextView.text = "Total: Rp $total"
        }

        viewModel.checkoutStatus.observe(this) { status ->
            when (status) {
                is CheckoutViewModel.CheckoutState.Loading -> tampilkanLoading()
                is CheckoutViewModel.CheckoutState.Success -> {
                    sembunyikanLoading()
                    Toast.makeText(this, "Pembayaran berhasil", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                }
                is CheckoutViewModel.CheckoutState.Error -> {
                    sembunyikanLoading()
                    Toast.makeText(this, "Pembayaran gagal: ${status.message}", Toast.LENGTH_SHORT).show()
                }
                is CheckoutViewModel.CheckoutState.Initial -> {
                    sembunyikanLoading()
                }
            }
        }

        viewModel.error.observe(this) { pesanError ->
            pesanError?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
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
        contentLayout.alpha = 0.5f
        checkoutButton.isEnabled = false
    }

    private fun sembunyikanLoading() {
        loadingProgressBar.isVisible = false
        contentLayout.alpha = 1.0f
        checkoutButton.isEnabled = true
    }
}