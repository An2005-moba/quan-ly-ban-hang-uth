package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nhom10.quanlybanhang.model.Customer
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CustomerRepositoryImpl : CustomerRepository {

    private val db = FirebaseFirestore.getInstance()
    private val customerCollection = db.collection("customers")

    override fun getCustomers(): Flow<List<Customer>> = callbackFlow {
        val listener = customerCollection
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

    override suspend fun addCustomer(customer: Customer) {
        try {
            // Nếu dùng @DocumentId, Firestore sẽ tự gán ID
            // Nếu bạn muốn ID tùy chỉnh, hãy dùng .document(id).set(customer)
            customerCollection.add(customer).await()
        } catch (e: Exception) {
            throw e
        }
    }
}