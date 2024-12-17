package com.example.healthcaremonitoringapp.ui.patient

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.network.DashboardRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = DashboardRepository()

    private val _healthSummary = MutableLiveData<String>()
    val healthSummary: LiveData<String> = _healthSummary

    private val _upcomingAppointments = MutableLiveData<List<Appointment>>()
    val upcomingAppointments: LiveData<List<Appointment>> = _upcomingAppointments

    private val _prescribedMedicines = MutableLiveData<List<Medicine>>()
    val prescribedMedicines: LiveData<List<Medicine>> = _prescribedMedicines

    private val _notifications = MutableLiveData<List<Notification>>()
    val notifications: LiveData<List<Notification>> = _notifications

    init {
        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        viewModelScope.launch {
            try {
                // Fetch health summary
                _healthSummary.value = repository.getHealthSummary()

                // Fetch upcoming appointments
                _upcomingAppointments.value = repository.getUpcomingAppointments()

                // Fetch prescribed medicines
                _prescribedMedicines.value = repository.getPrescribedMedicines()

                // Fetch notifications
                _notifications.value = repository.getNotifications()
            } catch (e: Exception) {
                // Handle errors
                // You might want to set error state or show error message
            }
        }
    }

    fun addMedicineToList(medicine: Medicine) {
        viewModelScope.launch {
            repository.addMedicineToList(medicine)
            // Refresh medicine list after adding
            _prescribedMedicines.value = repository.getPrescribedMedicines()
        }
    }

    fun updateMedicinePurchaseStatus(medicineId: String, status: String) {
        viewModelScope.launch {
            repository.updateMedicinePurchaseStatus(medicineId, status)
            // Refresh medicine list after updating
            _prescribedMedicines.value = repository.getPrescribedMedicines()
        }
    }
}