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
            val isGoogle = user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }

            val basicState = EditProfileUiState(
                userName = user.displayName ?: "Chưa đặt tên",
                photoUrl = user.photoUrl?.toString(),
                email = user.email ?: "",
                isGoogleLogin = isGoogle
            )
            _uiState.value = basicState

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

    /**
     * SỬA: nhận trực tiếp base64String thay vì Context + Uri
     * UI sẽ chịu trách nhiệm chuyển Uri -> Base64 (dùng contentResolver),
     * rồi gọi hàm này để upload / cập nhật state.
     */
    fun updateAvatar(base64String: String) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid ?: return@launch

            // updateAvatarBase64 : giả sử repo đã có hàm này
            val result = authRepo.updateAvatarBase64(userId, base64String)

            result.onSuccess {
                _uiState.value = _uiState.value.copy(photoUrl = base64String)
            }
            result.onFailure {
                // TODO: xử lý lỗi (thông báo, logging, set isUploading = false,...)
            }
        }
    }
}
