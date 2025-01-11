package com.example.healthcaremonitoringapp.ui.patient.checkout

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthcaremonitoringapp.models.Medicine
import com.example.healthcaremonitoringapp.models.PurchaseStatus
import com.example.healthcaremonitoringapp.network.DashboardPatientRepository
import com.example.healthcaremonitoringapp.network.RetrofitClient
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val repository = DashboardPatientRepository(RetrofitClient.apiService)

    private val _checkoutItems = MutableLiveData<List<Medicine>>(emptyList())
    val checkoutItems: LiveData<List<Medicine>> = _checkoutItems

    private val _totalPrice = MutableLiveData(0)
    val totalPrice: LiveData<Int> = _totalPrice

    private val _checkoutStatus = MutableLiveData<CheckoutState>()
    val checkoutStatus: LiveData<CheckoutState> = _checkoutStatus

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    sealed class CheckoutState {
        object Initial : CheckoutState()
        object Loading : CheckoutState()
        object Success : CheckoutState()
        data class Error(val message: String) : CheckoutState()
    }

    fun setCheckoutItems(items: List<Medicine>) {
        _checkoutItems.value = items
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        _totalPrice.value = _checkoutItems.value?.sumOf { it.price } ?: 0
    }

    fun addToCheckout(medicine: Medicine) {
        val currentItems = _checkoutItems.value?.toMutableList() ?: mutableListOf()
        if (!currentItems.any { it.id == medicine.id }) {
            currentItems.add(medicine)
            _checkoutItems.value = currentItems
            calculateTotal()
        }
    }

    fun addMultipleToCheckout(medicines: List<Medicine>) {
        val currentItems = _checkoutItems.value?.toMutableList() ?: mutableListOf()
        val newItems = medicines.filter { medicine ->
            !currentItems.any { it.id == medicine.id }
        }
        if (newItems.isNotEmpty()) {
            currentItems.addAll(newItems)
            _checkoutItems.value = currentItems
            calculateTotal()
        }
    }

    fun removeFromCheckout(medicineId: String) {
        val currentItems = _checkoutItems.value?.toMutableList() ?: mutableListOf()
        if (currentItems.removeIf { it.id == medicineId }) {
            _checkoutItems.value = currentItems
            calculateTotal()
        }
    }

    private fun calculateTotal() {
        val total = _checkoutItems.value?.sumOf { it.price } ?: 0
        _totalPrice.value = total
    }

    fun processCheckout() {
        viewModelScope.launch {
            try {
                _checkoutStatus.value = CheckoutState.Loading

                val items = _checkoutItems.value ?: emptyList()
                if (items.isEmpty()) {
                    _checkoutStatus.value = CheckoutState.Error("No items in checkout")
                    return@launch
                }

                // Process all medicines in parallel
                items.forEach { medicine ->
                    try {
                        repository.updateMedicinePurchaseStatus(
                            medicine.id,
                            PurchaseStatus.IN_PROGRESS.name
                        )
                    } catch (e: Exception) {
                        _error.value = "Failed to update status for ${medicine.medicine}: ${e.message}"
                    }
                }

                // Clear checkout after successful processing
                _checkoutItems.value = emptyList()
                _totalPrice.value = 0
                _checkoutStatus.value = CheckoutState.Success

            } catch (e: Exception) {
                _checkoutStatus.value = CheckoutState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun validateCheckout(): Boolean {
        return !_checkoutItems.value.isNullOrEmpty() &&
                _totalPrice.value != null &&
                _totalPrice.value!! > 0
    }
}