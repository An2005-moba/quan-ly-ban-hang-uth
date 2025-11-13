package com.nhom10.quanlybanhang.data

import com.google.firebase.firestore.FirebaseFirestore // THÊM IMPORT NÀY
import com.nhom10.quanlybanhang.model.Product
import com.google.android.gms.tasks.Tasks // THÊM IMPORT NÀY
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProductRepositoryImpl : ProductRepository {

    private val storage = FirebaseStorage.getInstance()
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
            Tasks.await(db.collection("products").add(product)) // Đây là Java SDK
        } catch (e: Exception) {
            throw e
        }
    }
    override suspend fun uploadImage(imageUri: Uri, tenFile: String): String {
        return try {
            // Tạo tham chiếu đến "product_images/ten_file.jpg"
            val storageRef = storage.reference.child("product_images/$tenFile")

            // Tải file lên
            storageRef.putFile(imageUri).await()

            // Lấy URL tải về
            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            throw e // Ném lỗi để ViewModel xử lý
        }
    }
}