package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage

class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

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

    override suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(authResult.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun updateUserProfile(displayName: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Chưa đăng nhập"))

        return try {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            user.updateProfile(profileUpdates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserDetails(userId: String): Result<Map<String, Any>> {
        return try {
            val document = db.collection("users").document(userId).get().await()
            if (document.exists()) {
                Result.success(document.data ?: emptyMap())
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateGender(userId: String, gender: String): Result<Unit> {
        return try {
            db.collection("users").document(userId).update("gioiTinh", gender).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun uploadAvatar(imageUri: android.net.Uri): Result<String> {
        val user = auth.currentUser ?: return Result.failure(Exception("Chưa đăng nhập"))

        return try {
            // 1. Tạo tên file: avatars/ID_CUA_USER.jpg
            // (Ghi đè ảnh cũ luôn cho tiết kiệm dung lượng)
            val storageRef = storage.reference.child("avatars/${user.uid}.jpg")

            // 2. Upload file lên
            storageRef.putFile(imageUri).await()

            // 3. Lấy đường link tải về (URL)
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // 4. Cập nhật link ảnh vào hồ sơ Auth (để hiện ngay lập tức)
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(android.net.Uri.parse(downloadUrl))
                .build()
            user.updateProfile(profileUpdates).await()

            // 5. Cập nhật link ảnh vào Firestore (để đồng bộ dữ liệu)
            db.collection("users").document(user.uid)
                .update("photoUrl", downloadUrl).await()

            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}