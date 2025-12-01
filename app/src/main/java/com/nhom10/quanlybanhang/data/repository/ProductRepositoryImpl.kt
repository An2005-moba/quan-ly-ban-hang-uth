package com.nhom10.quanlybanhang.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.data.model.OrderItem
import com.nhom10.quanlybanhang.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val db = FirebaseFirestore.getInstance()

    override fun getProducts(userId: String): Flow<List<Product>> = callbackFlow {
        // Thay vì "products", chúng ta truy cập collection con
        val collectionPath = db.collection("users").document(userId).collection("products")

        val listenerRegistration = collectionPath
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val products = snapshot.documents.mapNotNull { doc ->
                        // Đảm bảo documentId được gán cho model
                        doc.toObject(Product::class.java)?.copy(documentId = doc.id)
                    }
                    trySend(products)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun addProduct(userId: String, product: Product) {
        try {
            // Thêm sản phẩm vào collection con của user
            val collectionPath = db.collection("users").document(userId).collection("products")
            // Firestore sẽ tự động tạo ID và gán
            Tasks.await(collectionPath.add(product))
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProduct(userId: String, product: Product) {
        try {
            val docRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("products")
                .document(product.documentId)
            Tasks.await(docRef.set(product))
        } catch (e: Exception) {
            throw e
        }
    }

    // === HÀM MỚI: DELETE PRODUCT ===
    override suspend fun deleteProduct(userId: String, productId: String) {
        try {
            val docRef = db.collection("users")
                .document(userId)
                .collection("products")
                .document(productId) // Sử dụng productId được truyền vào

            // Chờ tác vụ xóa hoàn thành
            Tasks.await(docRef.delete())
        } catch (e: Exception) {
            // Ném exception để ViewModel có thể xử lý lỗi
            throw e
        }
    }
    // --- THÊM HÀM MỚI TẠI ĐÂY ---
    override suspend fun deductStock(userId: String, items: List<OrderItem>) {
        try {
            db.runTransaction { transaction ->
                val productsRef = db.collection("users").document(userId).collection("products")

                // BƯỚC 1: ĐỌC TOÀN BỘ DỮ LIỆU TRƯỚC (READ PHASE)
                // Lưu lại snapshot và reference để dùng cho bước sau
                val updateDataList = items.map { item ->
                    val docRef = productsRef.document(item.productId)
                    val snapshot = transaction.get(docRef)
                    // Trả về bộ ba: (Reference, Snapshot, Số lượng cần trừ)
                    Triple(docRef, snapshot, item.soLuong)
                }

                // BƯỚC 2: THỰC HIỆN GHI TOÀN BỘ (WRITE PHASE)
                for ((docRef, snapshot, deductAmount) in updateDataList) {
                    if (snapshot.exists()) {
                        val currentStock = snapshot.getDouble("soLuong") ?: 0.0
                        val newStock = currentStock - deductAmount

                        if (newStock <= 0.001) { // Coi như bằng 0 hoặc âm
                            // XÓA
                            transaction.delete(docRef)
                        } else {
                            // CẬP NHẬT
                            transaction.update(docRef, "soLuong", newStock)
                        }
                    }
                }
            }.await()
        } catch (e: Exception) {
            throw e
        }
    }
}
