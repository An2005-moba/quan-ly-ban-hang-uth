package com.nhom10.quanlybanhang.data

import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.model.Product
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepositoryImpl : ProductRepository {

    private val db = FirebaseFirestore.getInstance()

    // === SỬA HÀM NÀY ===
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
                        doc.toObject(Product::class.java)
                    }
                    trySend(products)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    // === SỬA HÀM NÀY ===
    override suspend fun addProduct(userId: String, product: Product) {
        try {
            // Thêm sản phẩm vào collection con của user
            val collectionPath = db.collection("users").document(userId).collection("products")
            Tasks.await(collectionPath.add(product))
        } catch (e: Exception) {
            throw e
        }
    }
}