package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override suspend fun registerUser(email: String, matKhau: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, matKhau).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun loginUser(email: String, matKhau: String): Result<FirebaseUser> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, matKhau).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun saveUserDetails(
        userId: String,
        hoTen: String,
        email: String,
        ngaySinh: String
    ): Result<Unit> {
        return try {
            val userData = hashMapOf(
                "uid" to userId,
                "hoTen" to hoTen,
                "email" to email,
                "ngaySinh" to ngaySinh
            )
            db.collection("users").document(userId).set(userData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}