package com.nhom10.quanlybanhang.data

import com.nhom10.quanlybanhang.model.Product
import kotlinx.coroutines.flow.Flow

// Interface định nghĩa các chức năng
interface ProductRepository {
    // Trả về một Flow để lắng nghe thay đổi
    fun getProducts(): Flow<List<Product>>

    // Dùng suspend fun cho các tác vụ 1 lần (thêm, sửa, xóa)
    suspend fun addProduct(product: Product)
}