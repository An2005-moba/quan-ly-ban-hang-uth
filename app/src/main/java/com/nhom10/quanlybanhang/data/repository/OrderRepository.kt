package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.model.Order

interface OrderRepository {
    // Lưu order
    suspend fun saveOrder(userId: String, order: Order): Result<Unit>

    // Lấy order theo userId
    suspend fun getOrders(userId: String): Result<List<Order>>
}
