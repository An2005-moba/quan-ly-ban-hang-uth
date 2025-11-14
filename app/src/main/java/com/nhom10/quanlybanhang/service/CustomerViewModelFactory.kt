package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhom10.quanlybanhang.data.repository.CustomerRepository

class CustomerViewModelFactory(
    private val repository: CustomerRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem có đúng là đang yêu cầu tạo CustomerViewModel không
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            // Nếu đúng, trả về một CustomerViewModel mới với repository đã cung cấp
            return CustomerViewModel(repository) as T
        }
        // Nếu không, báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}