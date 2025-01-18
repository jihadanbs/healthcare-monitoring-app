package com.example.healthcaremonitoringapp.network

import android.util.Log
import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.CheckoutItem
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.models.PurchaseStatus

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
        try {
            Log.d("Repository", "Attempting to fetch prescribed medicines")
            val response = apiService.getPrescribedMedicines()

            Log.d("Repository", "Response code: ${response.code()}")

            if (response.isSuccessful) {
                val medicines = response.body() ?: emptyList()
                Log.d("Repository", "Medicines fetched: $medicines")
                return medicines
            } else {
                // Log error body untuk detail lebih lanjut
                val errorBody = response.errorBody()?.string()
                Log.e("Repository", "Error response: $errorBody")
                throw Exception("Failed to fetch prescribed medicines: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Exception in getPrescribedMedicines", e)
            throw e
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

    suspend fun getCheckoutMedicines(): Result<List<CheckoutItem>> {
        return try {
            val response = apiService.getCheckoutMedicines()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun processCheckout(medicineId: String, amount: Int): Boolean {
        val response = apiService.processCheckout(
            PatientApiService.CheckoutRequest(medicineId, amount)
        )
        return response.isSuccessful
    }

    suspend fun updateMedicinePurchaseStatus(medicineId: String, status: String) {
        try {
            val validStatus = when(status) {
                "NOT_PURCHASED" -> "NOT_PURCHASED"
                "IN_PROGRESS" -> "IN_PROGRESS"
                "PURCHASED" -> "PURCHASED"
                else -> throw IllegalArgumentException("Invalid status: $status")
            }

            Log.d("Repository", "Sending request with ID: $medicineId and status: $validStatus")
            val statusRequest = PatientApiService.UpdateStatusRequest(status = validStatus)

            val response = apiService.updateMedicineStatus(medicineId, statusRequest)

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e("Repository", "Error response: $errorBody")
                throw Exception("Server error: $errorBody")
            }
        } catch (e: Exception) {
            Log.e("Repository", "Error updating medicine status", e)
            throw e
        }
    }

    suspend fun updateMedicineStatus(medicineId: String, status: PurchaseStatus): Result<Unit> {
        return try {
            val request = PatientApiService.UpdateStatusRequest(status.name)
            val response = apiService.updateMedicineStatus(medicineId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMedicinesInProgress(): Result<List<Medicine>> {
        return try {
            val response = apiService.getMedicinesInProgress()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

//    suspend fun updateMedicineStatus(
//        medicineId: String,
//        status: PurchaseStatus
//    ): Result<Medicine> {
//        return try {
//            val request = PatientApiService.MedicineStatusRequest(medicineId, status)
//            val response = apiService.updateMedicineStatus(request)
//            if (response.isSuccessful && response.body()?.success == true) {
//                Result.success(response.body()?.medicine!!)
//            } else {
//                Result.failure(Exception(response.body()?.message ?: "Unknown error"))
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}