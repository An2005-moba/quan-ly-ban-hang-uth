package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Data class chứa thông tin hiển thị
data class AccountUiState(
    val userName: String = "Chưa đăng nhập",
    val userId: String = "",
    val photoUrl: String? = null
)

class AccountViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val authRepo: AuthRepository = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    // Lắng nghe sự thay đổi đăng nhập/đăng xuất
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        // Khi trạng thái Auth thay đổi, tự động tải dữ liệu
        loadUserData()
    }

    init {
        auth.addAuthStateListener(authStateListener)
        // Tải lần đầu
        loadUserData()
    }

    // --- ĐÂY LÀ HÀM MÀ HOMESCREEN ĐANG TÌM ---
    fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            // 1. Lấy thông tin cơ bản từ Auth (để hiển thị nhanh)
            val authName = user.displayName ?: "Người dùng"
            val authPhoto = user.photoUrl?.toString()
            val id = "ID: ${user.uid.take(6)}..."

            // Cập nhật tạm thời
            _uiState.value = _uiState.value.copy(
                userName = authName,
                userId = id,
                photoUrl = authPhoto
            )

            // 2. Gọi Firestore để lấy dữ liệu MỚI NHẤT (Ảnh Base64, Tên mới sửa)
            viewModelScope.launch {
                val result = authRepo.getUserDetails(user.uid)
                result.onSuccess { data ->
                    val dbName = data["hoTen"] as? String
                    val dbPhoto = data["photoUrl"] as? String

                    // Nếu Firestore có dữ liệu thì ưu tiên dùng đè lên
                    _uiState.value = _uiState.value.copy(
                        userName = dbName ?: _uiState.value.userName,
                        photoUrl = dbPhoto ?: _uiState.value.photoUrl
                    )
                }
            }
        } else {
            // Nếu chưa đăng nhập
            _uiState.value = AccountUiState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}