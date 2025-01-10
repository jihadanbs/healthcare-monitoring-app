package com.example.healthcaremonitoringapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.Appointment
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.Notification
import com.example.healthcaremonitoringapp.network.DashboardPatientRepository
import com.example.healthcaremonitoringapp.network.RetrofitClient
import kotlinx.coroutines.launch

class DashboardPatientViewModel : ViewModel() {
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val repository = DashboardPatientRepository(RetrofitClient.apiService)

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
                val medicines = repository.getPrescribedMedicines()
                Log.d("DashboardViewModel", "Medicines fetched: $medicines")
                _prescribedMedicines.value = medicines
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Error fetching medicines", e)
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
            try {
                repository.updateMedicinePurchaseStatus(medicineId, status)
                // Refresh medicine list after updating
                _prescribedMedicines.value = repository.getPrescribedMedicines()
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("ViewModel", "Error updating medicine status", e)
            }
        }
    }

    fun fetchPrescribedMedicines() {
        viewModelScope.launch {
            try {
                val medicines = repository.getPrescribedMedicines()
                _prescribedMedicines.value = medicines
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
                Log.e("DashboardViewModel", "Error fetching prescribed medicines", e)
            }
        }
    }

}