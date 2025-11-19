package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.model.Order

interface OrderRepository {
    // SỬA: Thêm tham số userId
    suspend fun saveOrder(userId: String, order: Order): Result<Unit>
}