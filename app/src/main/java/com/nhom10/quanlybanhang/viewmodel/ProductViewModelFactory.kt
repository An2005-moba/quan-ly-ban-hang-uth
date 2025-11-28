package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nhom10.quanlybanhang.data.repository.ProductRepository

/**
 * Đây là lớp "hướng dẫn" (Factory) để tạo ra ProductViewModel.
 * Nó nhận ProductRepository làm tham số và truyền vào cho ViewModel.
 */
class ProductViewModelFactory(
    private val repository: ProductRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Kiểm tra xem có đúng là đang yêu cầu tạo ProductViewModel không
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            // Nếu đúng, trả về một ProductViewModel mới với repository đã cung cấp
            return ProductViewModel(repository) as T
        }
        // Nếu không, báo lỗi
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}