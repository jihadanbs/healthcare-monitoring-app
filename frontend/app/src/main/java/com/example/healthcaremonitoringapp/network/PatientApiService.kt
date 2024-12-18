package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PatientApiService {
    @GET("health/summary")
    suspend fun getHealthSummary(): Response<String>

    @GET("appointments/upcoming")
    suspend fun getUpcomingAppointments(): Response<List<Appointment>>

    @GET("dashboard/medicines/prescribed")
    suspend fun getPrescribedMedicines(): Response<List<Medicine>>

    @GET("notifications")
    suspend fun getNotifications(): Response<List<Notification>>

    @POST("medicines")
    suspend fun addMedicine(@Body medicine: Medicine): Response<Void>

    @POST("medicines/{id}/status")
    suspend fun updateMedicineStatus(@Path("id") medicineId: String, @Body status: String): Response<Void>
}