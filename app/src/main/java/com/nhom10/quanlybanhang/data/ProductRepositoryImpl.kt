package com.nhom10.quanlybanhang.data

import com.google.firebase.firestore.FirebaseFirestore // THÊM IMPORT NÀY
import com.nhom10.quanlybanhang.model.Product
import com.google.android.gms.tasks.Tasks // THÊM IMPORT NÀY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class ProductRepositoryImpl : ProductRepository {

    // SỬA DÒNG NÀY:
    // private val db = Firebase.firestore (Đây là KTX)
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
                        // SỬA DÒNG NÀY:
                        // doc.toObject<Product>() (Đây là KTX)
                        doc.toObject(Product::class.java) // Đây là Java SDK
                    }
                    trySend(products) // Gửi danh sách mới
                }
            }

        // Hủy lắng nghe khi Flow bị đóng
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun addProduct(product: Product) {
        try {
            // SỬA DÒNG NÀY:
            // db.collection("products").add(product).await() (Đây là KTX)
            Tasks.await(db.collection("products").add(product)) // Đây là Java SDK
        } catch (e: Exception) {
            // Xử lý lỗi (ví dụ: throw exception)
            throw e
        }
    }
}