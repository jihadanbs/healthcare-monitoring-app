package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import retrofit2.Response

class DashboardPatientRepository(private val apiService: PatientApiService) {

    suspend fun getHealthSummary(): String {
        val response = apiService.getHealthSummary()
        return if (response.isSuccessful) {
            response.body() ?: "No summary available"
        } else {
            "Error: ${response.message()}"
        }
    }

    suspend fun getUpcomingAppointments(): List<Appointment> {
        val response = apiService.getUpcomingAppointments()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList() // Handle error appropriately
        }
    }

    suspend fun getPrescribedMedicines(): List<Medicine> {
        val response = apiService.getPrescribedMedicines()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList() // Handle error appropriately
        }
    }

    suspend fun getNotifications(): List<Notification> {
        val response = apiService.getNotifications()
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList() // Handle error appropriately
        }
    }

    suspend fun addMedicineToList(medicine: Medicine) {
        val response = apiService.addMedicine(medicine)
        if (!response.isSuccessful) {
            // Handle error appropriately
        }
    }

    suspend fun updateMedicinePurchaseStatus(medicineId: String, status: String) {
        val response = apiService.updateMedicineStatus(medicineId, status)
        if (!response.isSuccessful) {
            // Handle error appropriately
        }
    }
}