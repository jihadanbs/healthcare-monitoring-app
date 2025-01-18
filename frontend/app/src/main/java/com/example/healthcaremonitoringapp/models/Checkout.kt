package com.example.healthcaremonitoringapp.models

data class CheckoutResponse(
    val success: Boolean,
    val data: List<CheckoutItem>
)

data class CheckoutItem(
    val recordId: String,
    val consultationDate: String,
    val doctor: Doctor,
    val medicines: List<Medicine>,
    val totalAmount: Int
)

data class Doctor(
    val name: String,
    val specialization: String?
)

data class StatusUpdateRequest(
    val status: String
)

data class UpdateCheckoutStatusRequest(
    val recordId: String,
    val medicineIds: List<String>,
    val status: PurchaseStatus
)

data class UpdateCheckoutStatusResponse(
    val success: Boolean,
    val message: String,
    val modifiedCount: Int
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)


