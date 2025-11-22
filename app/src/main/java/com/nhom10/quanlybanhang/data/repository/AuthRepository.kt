package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {

    suspend fun registerUser(email: String, matKhau: String): Result<FirebaseUser>

    suspend fun loginUser(email: String, matKhau: String): Result<FirebaseUser>

    suspend fun saveUserDetails(
        userId: String,
        hoTen: String,
        email: String,
        ngaySinh: String
    ): Result<Unit>

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser>
    fun getCurrentUser(): FirebaseUser?
    suspend fun updateUserProfile(displayName: String): Result<Unit>
    suspend fun getUserDetails(userId: String): Result<Map<String, Any>>
    suspend fun updateGender(userId: String, gender: String): Result<Unit>
    suspend fun uploadAvatar(imageUri: android.net.Uri): Result<String>
}