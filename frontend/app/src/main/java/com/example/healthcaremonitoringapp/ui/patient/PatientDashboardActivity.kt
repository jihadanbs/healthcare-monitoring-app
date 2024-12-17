package com.example.healthcaremonitoringapp.ui.patient

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthcaremonitoringapp.R
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.network.RetrofitClient
import kotlinx.android.synthetic.main.activity_patient_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_dashboard)

        // RecyclerView Setup
        appointmentRw.layoutManager = LinearLayoutManager(this)
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        medicineRecyclerView.layoutManager = LinearLayoutManager(this)

        fetchHealthSummary()
        fetchAppointments()
        fetchNotifications()
        fetchMedicines()
    }

    private fun fetchHealthSummary() {
        RetrofitClient.apiService.getHealthSummary().enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                healthSummaryTextView.text = response.body() ?: "Tidak ada data"
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                healthSummaryTextView.text = "Gagal memuat data"
            }
        })
    }

    private fun fetchAppointments() {
        RetrofitClient.apiService.getAppointments().enqueue(object : Callback<List<Appointment>> {
            override fun onResponse(call: Call<List<Appointment>>, response: Response<List<Appointment>>) {
                val adapter = AppointmentAdapter(response.body() ?: emptyList())
                appointmentRecyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<Appointment>>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun fetchNotifications() {
        RetrofitClient.apiService.getNotifications().enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                val adapter = NotificationAdapter(response.body() ?: emptyList())
                notificationRecyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun fetchMedicines() {
        RetrofitClient.apiService.getMedicines().enqueue(object : Callback<List<Medicine>> {
            override fun onResponse(call: Call<List<Medicine>>, response: Response<List<Medicine>>) {
                val adapter = MedicineAdapter(response.body() ?: emptyList())
                medicineRecyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<Medicine>>, t: Throwable) {
                // Handle error
            }
        })
    }
}