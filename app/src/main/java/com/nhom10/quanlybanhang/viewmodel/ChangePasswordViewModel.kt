package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChangePasswordUiState(
    val currentPassError: String? = null,
    val newPassError: String? = null,
    val confirmPassError: String? = null,
    val result: String? = null, // Thông báo thành công/thất bại
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false // Cờ báo hiệu để chuyển màn hình
)

class ChangePasswordViewModel : ViewModel() {

    private val authRepo: AuthRepository = AuthRepositoryImpl()

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun changePassword(current: String, new: String, confirm: String) {
        // Reset lỗi cũ
        _uiState.value = ChangePasswordUiState(isLoading = true)

        // --- 1. VALIDATION (KIỂM TRA LUẬT) ---
        if (current.isBlank()) {
            _uiState.value = _uiState.value.copy(currentPassError = "Vui lòng nhập mật khẩu hiện tại", isLoading = false)
            return
        }

        val newPassError = validatePassword(new)
        if (newPassError != null) {
            _uiState.value = _uiState.value.copy(newPassError = newPassError, isLoading = false)
            return
        }

        if (new != confirm) {
            _uiState.value = _uiState.value.copy(confirmPassError = "Mật khẩu xác nhận không khớp", isLoading = false)
            return
        }

        // --- 2. GỌI REPOSITORY ---
        viewModelScope.launch {
            val result = authRepo.changePassword(current, new)

            result.onSuccess {
                _uiState.value = ChangePasswordUiState(
                    result = "Đổi mật khẩu thành công!",
                    isSuccess = true,
                    isLoading = false
                )
            }

            result.onFailure { e ->
                // Lỗi thường gặp: "Mật khẩu cũ không đúng"
                _uiState.value = ChangePasswordUiState(
                    result = "Lỗi: ${e.message}", // Có thể là sai pass cũ
                    isLoading = false
                )
            }
        }
    }

    // Hàm kiểm tra độ mạnh mật khẩu (Copy từ AuthViewModel sang)
    private fun validatePassword(password: String): String? {
        if (password.length < 8) return "Mật khẩu phải có ít nhất 8 ký tự."
        if (!password[0].isUpperCase()) return "Mật khẩu phải bắt đầu bằng chữ viết hoa."
        val specialCharRegex = Regex("[^A-Za-z0-9]")
        if (!specialCharRegex.containsMatchIn(password)) return "Mật khẩu phải chứa ký tự đặc biệt."
        return null
    }

    fun resetState() {
        _uiState.value = ChangePasswordUiState()
    }
}