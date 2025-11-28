package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val userName: String = "",
    val photoUrl: String? = null,
    val email: String = "",
    val gender: String = "Chưa thiết lập",
    val isGoogleLogin: Boolean = false,
    val isUploading: Boolean = false,
    val error: String? = null
)

class EditProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val authRepo: AuthRepository = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            // Kiểm tra xem có phải đăng nhập bằng Google không
            val isGoogle = user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

            // Lấy thông tin cơ bản
            _uiState.value = EditProfileUiState(
                userName = user.displayName ?: "",
                photoUrl = user.photoUrl?.toString(),
                email = user.email ?: "",
                isGoogleLogin = isGoogle
            )

            // Lấy thông tin chi tiết từ Firestore (Giới tính, Ảnh Base64)
            viewModelScope.launch {
                val result = authRepo.getUserDetails(user.uid)
                result.onSuccess { data ->
                    val genderFromDb = data["gioiTinh"] as? String ?: "Chưa thiết lập"
                    val dbPhoto = data["photoUrl"] as? String
                    val dbName = data["hoTen"] as? String

                    _uiState.value = _uiState.value.copy(
                        gender = genderFromDb,
                        // Ưu tiên dữ liệu từ DB hơn Auth
                        photoUrl = dbPhoto ?: _uiState.value.photoUrl,
                        userName = dbName ?: _uiState.value.userName
                    )
                }
            }
        }
    }

    fun updateGender(newGender: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            authRepo.updateGender(userId, newGender)
            _uiState.value = _uiState.value.copy(gender = newGender)
        }
    }

    fun updateName(newName: String) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            // 1. Cập nhật Auth (để hiển thị nhanh)
            authRepo.updateUserProfile(newName)
            // 2. Cập nhật UI State
            _uiState.value = _uiState.value.copy(userName = newName)

            // LƯU Ý: Để đồng bộ hoàn toàn, bạn nên thêm hàm updateName trong Repository
            // để lưu vào Firestore field "hoTen". Hiện tại ta cập nhật Auth Profile trước.
        }
    }

    fun updateAvatar(base64String: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)

            // Gọi Repository để lưu chuỗi Base64 vào Firestore
            val result = authRepo.updateAvatarBase64(userId, base64String)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    photoUrl = base64String,
                    isUploading = false,
                    error = null
                )
            }
            result.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    error = "Lỗi lưu ảnh: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}