package com.example.healthcaremonitoringapp.models

data class Medicine(
    val id: String,
    val medicine: String,
    val dosage: String,
    val frequency: String,
    val status: PurchaseStatus,
)

enum class PurchaseStatus {
    NOT_PURCHASED,
    IN_PROGRESS,
    PURCHASED
}