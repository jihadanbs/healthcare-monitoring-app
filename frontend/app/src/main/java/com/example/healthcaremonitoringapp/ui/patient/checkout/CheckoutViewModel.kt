package com.example.healthcaremonitoringapp.ui.patient.checkout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.CheckoutItem
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.PurchaseStatus
import com.example.healthcaremonitoringapp.network.DashboardPatientRepository
import kotlinx.coroutines.launch

class CheckoutViewModel(private val repository: DashboardPatientRepository) : ViewModel() {
    private val _checkoutItems = MutableLiveData<List<CheckoutItem>>()
    val checkoutItems: LiveData<List<CheckoutItem>> = _checkoutItems

    private val _selectedMedicines = MutableLiveData<List<Medicine>>()
    val selectedMedicines: LiveData<List<Medicine>> = _selectedMedicines

    private val _checkoutState = MutableLiveData<CheckoutState>(CheckoutState.Initial)
    val checkoutState: LiveData<CheckoutState> = _checkoutState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> = _totalPrice

    init {
        loadCheckoutItems()
    }

    // Menggantikan loadInProgressMedicines dengan loadCheckoutItems
    fun loadInProgressMedicines() {
        loadCheckoutItems()
    }

    fun loadCheckoutItems() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                repository.getCheckoutMedicines()
                    .onSuccess { response ->
                        val medicines = response.flatMap { item -> item.medicines }
                        _selectedMedicines.value = medicines
                        _checkoutItems.value = response
                        calculateTotal()
                        _checkoutState.value = CheckoutState.Success(isCheckoutProcess = false)
                    }
                    .onFailure { exception ->
                        _error.value = exception.message
                        _checkoutState.value = CheckoutState.Error(exception.message ?: "Unknown error")
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _checkoutState.value = CheckoutState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun setCheckoutItems(items: List<Medicine>) {
        _selectedMedicines.value = items
        calculateTotal()
    }

    fun removeFromCheckout(medicineId: String) {
        viewModelScope.launch {
            try {
                repository.updateMedicineStatus(medicineId, PurchaseStatus.NOT_PURCHASED)
                    .onSuccess {
                        loadCheckoutItems() // Reload data after successful update
                    }
                    .onFailure { exception ->
                        _error.value = exception.message
                    }
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun validateCheckout(): Boolean {
        return !_selectedMedicines.value.isNullOrEmpty() && _totalPrice.value != null && _totalPrice.value!! > 0
    }

//    fun processCheckout() {
//        viewModelScope.launch {
//            _checkoutState.value = CheckoutState.Loading
//            try {
//                var success = true
//                _selectedMedicines.value?.forEach { medicine ->
//                    repository.updateMedicineStatus(medicine.id, PurchaseStatus.PURCHASED)
//                        .onFailure {
//                            success = false
//                            return@forEach
//                        }
//                }
//
//                if (success) {
//                    _checkoutState.value = CheckoutState.Success(isCheckoutProcess = true)
//                    _selectedMedicines.value = emptyList()
//                    calculateTotal()
//                } else {
//                    _checkoutState.value = CheckoutState.Error("Gagal memproses checkout")
//                }
//            } catch (e: Exception) {
//                _checkoutState.value = CheckoutState.Error(e.message ?: "Unknown error")
//            }
//        }
//    }

    fun processCheckout() {
        viewModelScope.launch {
            _checkoutState.value = CheckoutState.Loading
            try {
                var success = true
                _selectedMedicines.value?.forEach { medicine ->
                    repository.updateMedicineStatus(medicine.id, PurchaseStatus.PURCHASED)
                        .onFailure {
                            success = false
                            return@forEach
                        }
                }

                if (success) {
                    // Bersihkan semua data
                    _selectedMedicines.value = emptyList()
                    _checkoutItems.value = emptyList()
                    _totalPrice.value = 0
                    _checkoutState.value = CheckoutState.Success(isCheckoutProcess = true)

                    // Reload data untuk memastikan sinkronisasi dengan server
                    loadCheckoutItems()
                } else {
                    _checkoutState.value = CheckoutState.Error("Gagal memproses checkout")
                }
            } catch (e: Exception) {
                _checkoutState.value = CheckoutState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    private fun calculateTotal() {
        _totalPrice.value = _selectedMedicines.value?.sumOf { it.price } ?: 0
    }

    sealed class CheckoutState {
        object Initial : CheckoutState()
        object Loading : CheckoutState()
        data class Success(val isCheckoutProcess: Boolean = false) : CheckoutState()
        data class Error(val message: String) : CheckoutState()
    }
}