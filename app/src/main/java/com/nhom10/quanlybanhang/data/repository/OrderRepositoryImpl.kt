package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveOrder(userId: String, order: Order): Result<Unit> {
        return try {
            // SỬA: Đường dẫn lưu vào sub-collection
            val collectionPath = db.collection("users").document(userId).collection("orders")

            collectionPath.add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}