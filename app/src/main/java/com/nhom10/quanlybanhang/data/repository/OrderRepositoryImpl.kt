package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.data.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveOrder(userId: String, order: Order): Result<Unit> {
        return try {
            val collectionPath = db.collection("users").document(userId).collection("orders")
            collectionPath.add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("orders")
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
