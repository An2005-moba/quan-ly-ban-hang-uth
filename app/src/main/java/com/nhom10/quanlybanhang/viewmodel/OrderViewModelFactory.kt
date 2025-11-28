package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhom10.quanlybanhang.data.repository.OrderRepository

class OrderViewModelFactory(
    private val repository: OrderRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem có đúng là đang yêu cầu tạo OrderViewModel không
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            // Nếu đúng, trả về một OrderViewModel mới với repository đã cung cấp
            return OrderViewModel(repository) as T
        }
        // Nếu không, báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}