package com.nhom10.quanlybanhang.data.repository

import com.nhom10.quanlybanhang.data.model.Customer
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    // SỬA: Thêm userId để biết lấy khách của ai
    fun getCustomers(userId: String): Flow<List<Customer>>

    // SỬA: Thêm userId để biết thêm vào danh sách của ai
    suspend fun addCustomer(userId: String, customer: Customer)
    suspend fun deleteCustomer(userId: String, customerId: String)
    suspend fun deleteAllCustomers(userId: String)
}