package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.CustomerRepository
import com.nhom10.quanlybanhang.model.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            repository.getCustomers()
                .catch { e -> /* Xử lý lỗi */ }
                .collect { _customers.value = it }
        }
    }

    fun addCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                repository.addCustomer(customer)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}