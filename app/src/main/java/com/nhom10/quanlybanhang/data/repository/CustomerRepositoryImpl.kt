package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhom10.quanlybanhang.data.model.Customer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CustomerRepositoryImpl : CustomerRepository {

    private val db = FirebaseFirestore.getInstance()

    // SỬA: Lấy danh sách từ sub-collection
    override fun getCustomers(userId: String): Flow<List<Customer>> = callbackFlow {
        val collectionPath = db.collection("users").document(userId).collection("customers")

        val listener = collectionPath
            .orderBy("tenKhachHang", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val customers = snapshot.toObjects(Customer::class.java)
                    trySend(customers)
                }
            }
        awaitClose { listener.remove() }
    }

    // SỬA: Thêm vào sub-collection
    override suspend fun addCustomer(userId: String, customer: Customer) {
        try {
            val collectionPath = db.collection("users").document(userId).collection("customers")
            // Dùng Tasks.await hoặc .await() (kotlinx-coroutines-play-services)
            collectionPath.add(customer).await()
        } catch (e: Exception) {
            throw e
        }
    }
}