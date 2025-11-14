package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.model.Order
import kotlinx.coroutines.tasks.await

class OrderRepositoryImpl : OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val orderCollection = db.collection("orders")

    override suspend fun saveOrder(order: Order): Result<Unit> {
        return try {
            orderCollection.add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}