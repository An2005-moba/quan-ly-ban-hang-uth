package com.nhom10.quanlybanhang.service

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.CustomerRepository
import com.nhom10.quanlybanhang.model.Customer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    init {
        loadCustomers()
    }

    // SỬA: Lấy ID và gọi repo
    private fun loadCustomers() {
        val userId = currentUserId
        if (userId == null) {
            _customers.value = emptyList()
            return
        }

        viewModelScope.launch {
            repository.getCustomers(userId) // Truyền userId
                .catch { e -> Log.e("CustomerViewModel", "Lỗi tải khách: ", e) }
                .collect { _customers.value = it }
        }
    }

    // SỬA: Lấy ID và gọi repo
    fun addCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Chưa đăng nhập"))
            return
        }

        viewModelScope.launch {
            try {
                repository.addCustomer(userId, customer) // Truyền userId
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}