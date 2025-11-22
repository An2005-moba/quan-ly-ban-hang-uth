package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider // <-- Import này
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
    val isUploading: Boolean = false
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
            // 1. Kiểm tra xem có phải Google không?
            // Duyệt qua danh sách provider, nếu thấy google.com thì là true
            val isGoogle = user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

            // 2. Lấy thông tin cơ bản
            val basicState = EditProfileUiState(
                userName = user.displayName ?: "Chưa đặt tên",
                photoUrl = user.photoUrl?.toString(),
                email = user.email ?: "",
                isGoogleLogin = isGoogle // <-- Gán vào state
            )
            _uiState.value = basicState

            // 3. Lấy Giới tính từ Firestore
            viewModelScope.launch {
                val result = authRepo.getUserDetails(user.uid)
                result.onSuccess { data ->
                    val genderFromDb = data["gioiTinh"] as? String ?: "Chưa thiết lập"
                    _uiState.value = _uiState.value.copy(gender = genderFromDb)
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
            authRepo.updateUserProfile(newName)
            _uiState.value = _uiState.value.copy(userName = newName)
        }
    }

    fun uploadAvatar(uri: android.net.Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true) // Bắt đầu xoay

            val result = authRepo.uploadAvatar(uri)

            result.onSuccess { newUrl ->
                // Cập nhật UI ngay lập tức với ảnh mới
                _uiState.value = _uiState.value.copy(
                    photoUrl = newUrl,
                    isUploading = false
                )
            }
            result.onFailure {
                _uiState.value = _uiState.value.copy(isUploading = false)
                // Có thể thêm biến error để báo lỗi nếu muốn
            }
        }
    }
}