package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.data.model.OrderItem
import com.nhom10.quanlybanhang.data.model.Product
import kotlinx.coroutines.flow.Flow

// Interface định nghĩa các chức năng
interface ProductRepository {
    // SỬA: Thêm userId
    fun getProducts(userId: String): Flow<List<Product>>

    // SỬA: Thêm userId
    suspend fun addProduct(userId: String, product: Product)

    // Hàm cập nhật sản phẩm
    suspend fun updateProduct(userId: String, product: Product)

    // Hàm xóa sản phẩm: Định nghĩa là một hàm trừu tượng
    suspend fun deleteProduct(userId: String, productId: String)
    suspend fun deductStock(userId: String, items: List<OrderItem>)

}