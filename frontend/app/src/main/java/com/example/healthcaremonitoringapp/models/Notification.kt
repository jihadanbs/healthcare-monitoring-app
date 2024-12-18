package com.example.healthcaremonitoringapp.models

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val date: Date,
    val isRead: Boolean = false
)

enum class NotificationType {
    APPOINTMENT_REMINDER,
    MEDICAL_RECORD,
    PRESCRIPTION,
    GENERAL
}