package com.example.healthcaremonitoringapp.network

import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.CheckoutItem
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.models.PurchaseStatus
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

    @POST("dashboard/medicines")
    suspend fun addMedicine(@Body medicine: Medicine): Response<Void>

    @POST("dashboard/medicines/{id}/status")
    suspend fun updateMedicineStatus(@Path("id") medicineId: String, @Body statusRequest: UpdateStatusRequest): Response<UpdateStatusResponse>

    data class UpdateStatusRequest(val status: String)

    data class UpdateStatusResponse(val message: String, val prescription: Medicine)

    @GET("dashboard/checkout/medicines")
    suspend fun getCheckoutMedicines(): Response<CheckoutResponse>

    @GET("dashboard/medicines/in-progress")
    suspend fun getMedicinesInProgress(): Response<List<Medicine>>

    @POST("dashboard/checkout/process")
    suspend fun processCheckout(@Body checkoutRequest: CheckoutRequest): Response<CheckoutResponse>

    data class CheckoutRequest(
        val medicineId: String,
        val amount: Int
    )

    data class CheckoutResponse(
        val success: Boolean,
        val message: String,
        val data: List<CheckoutItem>
    )

    @POST("dashboard/medicines/update-status")
    suspend fun updateMedicineStatus(@Body statusRequest: MedicineStatusRequest): Response<MedicineStatusResponse>

    data class MedicineStatusRequest(
        val medicineId: String,
        val status: PurchaseStatus
    )

    data class MedicineStatusResponse(
        val success: Boolean,
        val message: String,
        val medicine: Medicine
    )
}