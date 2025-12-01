package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.data.model.Order
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import com.google.firebase.firestore.Query

class OrderRepositoryImpl : OrderRepository {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun saveOrder(userId: String, order: Order): Result<Unit> {
        return try {
            db.collection("users").document(userId)
                .collection("orders").document(order.id)
                .set(order)
                .await()
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
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()

            // SỬA: Lấy TẤT CẢ (bao gồm cả "Đã xóa") để ReportViewModel có dữ liệu tính toán
            val list = snapshot.documents.mapNotNull { document ->
                document.toObject(Order::class.java)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // SỬA: Quay lại Soft Delete (Cập nhật status thay vì delete)
    override suspend fun deleteOrder(userId: String, orderId: String): Result<Unit> {
        return try {
            db.collection("users").document(userId)
                .collection("orders").document(orderId)
                .update("status", "Đã xóa") // Chỉ đánh dấu là đã xóa
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}