package com.example.healthcaremonitoringapp.ui.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.viewmodels.DashboardPatientViewModel

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
}