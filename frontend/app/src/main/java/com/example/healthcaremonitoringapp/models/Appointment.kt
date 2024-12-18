package com.example.healthcaremonitoringapp.models

import java.util.Date

data class Appointment(
    val id: String,
    val doctorName: String,
    val speciality: String,
    val date: Date,
    val time: String,
    val status: AppointmentStatus
)

enum class AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}