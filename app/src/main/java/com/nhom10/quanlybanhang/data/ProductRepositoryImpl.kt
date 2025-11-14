package com.nhom10.quanlybanhang.data

import com.google.firebase.firestore.FirebaseFirestore // THÊM IMPORT NÀY
import com.nhom10.quanlybanhang.model.Product
import com.google.android.gms.tasks.Tasks // THÊM IMPORT NÀY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepositoryImpl : ProductRepository {
    private val db = FirebaseFirestore.getInstance() // Đây là Java SDK

    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        // Lắng nghe realtime
        val listenerRegistration = db.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Đóng Flow nếu có lỗi
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

    override suspend fun addProduct(product: Product) {
        try {
            Tasks.await(db.collection("products").add(product)) // Đây là Java SDK
        } catch (e: Exception) {
            throw e
        }
    }

}