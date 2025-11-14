package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    // Lấy danh sách khách hàng (real-time)
    fun getCustomers(): Flow<List<Customer>>

    // Thêm khách hàng mới
    suspend fun addCustomer(customer: Customer)

    // (Bạn có thể thêm suspend fun updateCustomer, deleteCustomer sau)
}