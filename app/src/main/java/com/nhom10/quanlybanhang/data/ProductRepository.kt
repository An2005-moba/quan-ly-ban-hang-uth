package com.nhom10.quanlybanhang.data

import com.nhom10.quanlybanhang.model.Product
import kotlinx.coroutines.flow.Flow


// Interface định nghĩa các chức năng
interface ProductRepository {
    // SỬA: Thêm userId
    fun getProducts(userId: String): Flow<List<Product>>

    // SỬA: Thêm userId
    suspend fun addProduct(userId: String, product: Product)
    suspend fun updateProduct(userId: String, product: Product)

}