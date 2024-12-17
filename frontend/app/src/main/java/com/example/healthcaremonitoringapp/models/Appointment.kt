package com.example.healthcaremonitoringapp.models

data class Appointment(
    val id: String,
    val date: String,
    val time: String,
    val doctorName: String,
    val status: String
)