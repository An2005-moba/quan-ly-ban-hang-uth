package com.nhom10.quanlybanhang.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Data Class mới để giữ trạng thái cho Login UI
 */
data class LoginUiState(
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginResult: String? = null,
    val isLoading: Boolean = false
)

class LoginViewModel : ViewModel() {
    private val authRepo: AuthRepository = AuthRepositoryImpl()
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginUser(email: String, matKhau: String) {
        val cleanEmail = email.trim()
        val cleanMatKhau = matKhau.trim()
        val emailError = if (cleanEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) "Email không hợp lệ." else null
        val passwordError = if (cleanMatKhau.isBlank()) "Vui lòng nhập mật khẩu." else null
        _uiState.value = LoginUiState(
            emailError = emailError,
            passwordError = passwordError
        )
        if (emailError != null || passwordError != null) {
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val loginResult = authRepo.loginUser(cleanEmail, cleanMatKhau)

            loginResult.onSuccess { user ->
                _uiState.value = LoginUiState(loginResult = "Đăng nhập thành công!")
            }

            loginResult.onFailure { exception ->
                _uiState.value = LoginUiState(loginResult = "Đăng nhập thất bại: Sai email hoặc mật khẩu.")
            }
        }
    }
    fun resetLoginResult() {
        _uiState.value = _uiState.value.copy(loginResult = null)
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)

            val result = authRepo.signInWithGoogle(idToken)

            result.onSuccess { user ->
                _uiState.value = LoginUiState(loginResult = "Đăng nhập thành công!")
            }
            result.onFailure { e ->
                _uiState.value = LoginUiState(loginResult = "Lỗi Google: ${e.message}")
            }
        }
    }
}