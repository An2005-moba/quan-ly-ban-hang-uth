package com.nhom10.quanlybanhang.data.repository

import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.data.model.Product
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
}