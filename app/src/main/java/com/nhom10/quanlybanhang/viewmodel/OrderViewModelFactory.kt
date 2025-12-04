package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.data.repository.ProductRepository

class OrderViewModelFactory(
    private val repository: OrderRepository,
    private val productRepository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            // Truyền cả 2 repo vào ViewModel
            return OrderViewModel(repository, productRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}