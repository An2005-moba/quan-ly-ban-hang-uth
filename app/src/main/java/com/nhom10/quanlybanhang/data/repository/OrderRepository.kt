package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.model.Order

interface OrderRepository {
    suspend fun saveOrder(order: Order): Result<Unit>
    // (Bạn có thể thêm getOrderHistory() sau)
}