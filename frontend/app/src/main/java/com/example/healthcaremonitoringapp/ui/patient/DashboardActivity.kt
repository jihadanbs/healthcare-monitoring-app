package com.example.healthcaremonitoringapp.ui.patient

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.PurchaseStatus
import com.example.healthcaremonitoringapp.ui.patient.checkout.CheckoutActivity
import com.example.healthcaremonitoringapp.viewmodels.DashboardPatientViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.UUID

class DashboardActivity : AppCompatActivity() {
    private lateinit var viewModel: DashboardPatientViewModel
    private lateinit var medicineListAdapter: MedicineListAdapter
    private lateinit var appointmentRecyclerView: RecyclerView
    private lateinit var medicineRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(DashboardPatientViewModel::class.java)

        // Setup RecyclerViews
        appointmentRecyclerView = findViewById(R.id.appointmentRecyclerView)
        medicineRecyclerView = findViewById(R.id.medicineRecyclerView)

        // Setup Adapters
        setupAppointmentList()
        setupMedicineList()

        // Observe LiveData
        observeDashboardData()

        // error handling untuk viewModel
        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
                R.id.navigation_checkout -> {
                    startActivity(Intent(this, CheckoutActivity::class.java))
                    true
                }
                R.id.navigation_account -> {
                    // Handle account navigation
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.navigation_home
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh data setelah checkout berhasil
            viewModel.fetchPrescribedMedicines()
        }
    }

    private fun setupAppointmentList() {
        val appointmentAdapter = AppointmentAdapter()
        appointmentRecyclerView.layoutManager = LinearLayoutManager(this)
        appointmentRecyclerView.adapter = appointmentAdapter

        viewModel.upcomingAppointments.observe(this) { appointments ->
            appointmentAdapter.submitList(appointments)
        }
    }

    private fun setupMedicineList() {
        medicineListAdapter = MedicineListAdapter(
            onStatusChangeListener = { medicine, status ->
                viewModel.updateMedicinePurchaseStatus(medicine.id, status.name)
            },
            onCheckoutClick = { medicine ->
                // Tambahkan log untuk memastikan data medicine tidak null
                Log.d("DashboardActivity", "Medicine to checkout: $medicine")

                // Pastikan medicine adalah Parcelable
                val intent = Intent(this, CheckoutActivity::class.java)
                intent.putExtra("SELECTED_MEDICINE", medicine)

                // Gunakan startActivity dengan requestCode untuk bisa menerima hasil
                startActivityForResult(intent, CHECKOUT_REQUEST_CODE)
            }
        )

        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineRecyclerView.adapter = medicineListAdapter

        viewModel.prescribedMedicines.observe(this) { medicines ->
            medicineListAdapter.submitList(medicines)
        }
    }

    companion object {
        const val CHECKOUT_REQUEST_CODE = 100
    }

    private fun observeDashboardData() {
        viewModel.healthSummary.observe(this) { summary ->
            // Update health summary UI elements
        }

        viewModel.notifications.observe(this) { notifications ->
            // Handle notifications
        }
    }

    private fun showAddMedicineDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_medicine, null)
        val medicineNameInput = dialogView.findViewById<EditText>(R.id.medicineNameInput)
        val dosageInput = dialogView.findViewById<EditText>(R.id.dosageInput)
        val frequencyInput = dialogView.findViewById<EditText>(R.id.frequencyInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.priceInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add Medicine")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val medicineName = medicineNameInput.text.toString()
                val dosage = dosageInput.text.toString()
                val frequency = frequencyInput.text.toString()
                val price = priceInput.text.toString().toIntOrNull() ?: 0

                if (medicineName.isNotEmpty() && dosage.isNotEmpty() && frequency.isNotEmpty()) {
                    val newMedicine = Medicine(
                        id = UUID.randomUUID().toString(),
                        medicine = medicineName,
                        dosage = dosage,
                        frequency = frequency,
                        status = PurchaseStatus.NOT_PURCHASED,
                        price = price,
                        medicalRecordId = ""
                    )
                    // Panggil ViewModel untuk menambahkan data
                    viewModel.addMedicineToList(newMedicine)
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }
}