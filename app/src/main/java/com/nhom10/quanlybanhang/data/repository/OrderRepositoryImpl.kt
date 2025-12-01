package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.data.model.Order
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import com.google.firebase.firestore.Query

class OrderRepositoryImpl : OrderRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- Cải tiến: Dùng order.id làm Document ID ---
    override suspend fun saveOrder(userId: String, order: Order): Result<Unit> {
        return try {
            // Sử dụng order.id làm Document ID để việc xóa và truy xuất sau này dễ dàng hơn
            db.collection("users").document(userId)
                .collection("orders").document(order.id)
                .set(order) // Dùng set() thay vì add()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Cải tiến: Sắp xếp theo ngày mới nhất ---
    override suspend fun getOrders(userId: String): Result<List<Order>> {
        return try {
            val snapshot = db.collection("users")
                .document(userId)
                .collection("orders")
                .orderBy("date", Query.Direction.DESCENDING) // Sắp xếp theo ngày mới nhất
                .get()
                .await()

            val list = snapshot.documents.mapNotNull { document ->
                // Lấy đối tượng Order và thêm ID của Firebase Document (mặc dù ta đã dùng order.id làm Document ID)
                document.toObject(Order::class.java)
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ĐÃ THÊM: Triển khai deleteOrder ---
    override suspend fun deleteOrder(userId: String, orderId: String): Result<Unit> {
        return try {
            db.collection("users").document(userId)
                .collection("orders").document(orderId)
                .update("status", "Đã xóa") // Soft Delete
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}