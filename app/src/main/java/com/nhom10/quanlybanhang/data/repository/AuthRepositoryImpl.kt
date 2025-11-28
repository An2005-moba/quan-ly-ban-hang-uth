package com.nhom10.quanlybanhang.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.auth.EmailAuthProvider

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

    override suspend fun updateAvatarBase64(userId: String, base64String: String): Result<Unit> {
        return try {
            // Lưu chuỗi Base64 thẳng vào field "photoUrl" trong Firestore
            db.collection("users").document(userId)
                .update("photoUrl", base64String).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(currentPass: String, newPass: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("Chưa đăng nhập"))
        val email = user.email ?: return Result.failure(Exception("Không tìm thấy email"))

        return try {
            // BƯỚC 1: Tạo thông tin xác thực từ mật khẩu cũ
            val credential = EmailAuthProvider.getCredential(email, currentPass)

            // BƯỚC 2: Xác thực lại (Re-authenticate)
            user.reauthenticate(credential).await()

            // BƯỚC 3: Nếu bước 2 ok, tiến hành đổi mật khẩu mới
            user.updatePassword(newPass).await()

            Result.success(Unit)
        } catch (e: Exception) {
            // Sai mật khẩu cũ, hoặc mật khẩu mới quá yếu...
            Result.failure(e)
        }
    }
}