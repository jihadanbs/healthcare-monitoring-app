package com.example.healthcaremonitoringapp.ui.patient

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.PurchaseStatus
import com.example.healthcaremonitoringapp.viewmodels.DashboardPatientViewModel
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

        findViewById<Button>(R.id.addMedicineButton).setOnClickListener {
            showAddMedicineDialog()
        }

        // error handling untuk viewModel
        viewModel.error.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
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
        // Tambahkan parameter onStatusChangeListener di sini
        medicineListAdapter = MedicineListAdapter { medicine, status ->
            // Panggil metode update status di ViewModel
            viewModel.updateMedicinePurchaseStatus(medicine.id, status.name)
        }

        medicineRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineRecyclerView.adapter = medicineListAdapter

        viewModel.prescribedMedicines.observe(this) { medicines ->
            medicineListAdapter.submitList(medicines)
        }
    }

    private fun observeDashboardData() {
        viewModel.healthSummary.observe(this) { summary ->
            // Update health summary UI elements
            // For example: findViewById<TextView>(R.id.healthSummaryTextView).text = summary
        }

        viewModel.notifications.observe(this) { notifications ->
            // Handle notifications
            // You might want to show a badge or update a notification list
        }
    }

    private fun showAddMedicineDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_medicine, null)
        val medicineNameInput = dialogView.findViewById<EditText>(R.id.medicineNameInput)
        val dosageInput = dialogView.findViewById<EditText>(R.id.dosageInput)
        val frequencyInput = dialogView.findViewById<EditText>(R.id.frequencyInput)
        val priceInput = dialogView.findViewById<EditText>(R.id.priceInput)

        AlertDialog.Builder(this)
            .setTitle("Tambah Obat Baru")
            .setView(dialogView)
            .setPositiveButton("Tambah") { _, _ ->
                val medicineName = medicineNameInput.text.toString()
                val dosage = dosageInput.text.toString()
                val frequency = frequencyInput.text.toString()
                val priceText = priceInput.text.toString()

                // Validasi input dan konversi harga
                if (medicineName.isNotBlank() && dosage.isNotBlank() && frequency.isNotBlank() && priceText.isNotBlank()) {
                    try {
                        val price = priceText.toInt() // Konversi String ke Int

                        val newMedicine = Medicine(
                            id = UUID.randomUUID().toString(), // Generate temporary ID
                            medicine = medicineName,
                            dosage = dosage,
                            frequency = frequency,
                            status = PurchaseStatus.NOT_PURCHASED,
                            price = price
                        )
                        viewModel.addMedicineToList(newMedicine)
                    } catch (e: NumberFormatException) {
                        // Tampilkan pesan error jika input harga tidak valid
                        Toast.makeText(this, "Harga harus berupa angka valid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }
}