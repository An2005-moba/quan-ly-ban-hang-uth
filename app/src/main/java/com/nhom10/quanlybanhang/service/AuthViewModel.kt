package com.nhom10.quanlybanhang.service

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val hoTenError: String? = null,
    val emailError: String? = null,
    val ngaySinhError: String? = null,
    val matKhauError: String? = null,
    val xacNhanMatKhauError: String? = null,
    val registrationResult: String? = null,
    val isLoading: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val authRepo: AuthRepository = AuthRepositoryImpl()
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState
    fun registerUser(
        email: String,
        matKhau: String,
        xacNhanMatKhau: String,
        hoTen: String,
        ngaySinh: String
    ) {
        val cleanEmail = email.trim()
        val cleanMatKhau = matKhau.trim()
        val cleanHoTen = hoTen.trim()
        val hoTenError = if (cleanHoTen.isBlank()) "Vui lòng nhập Họ Tên." else null
        val emailError = if (cleanEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) "Email không hợp lệ." else null
        val ngaySinhError = if (ngaySinh.isBlank()) "Vui lòng chọn ngày sinh." else null
        val matKhauError = validatePassword(cleanMatKhau)
        val xacNhanError = if (cleanMatKhau != xacNhanMatKhau) "Mật khẩu xác nhận không khớp!" else null

        _uiState.value = RegisterUiState(
            hoTenError = hoTenError,
            emailError = emailError,
            ngaySinhError = ngaySinhError,
            matKhauError = matKhauError,
            xacNhanMatKhauError = xacNhanError
        )

        if (hoTenError != null || emailError != null || ngaySinhError != null || matKhauError != null || xacNhanError != null) {
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)
            val authResult = authRepo.registerUser(cleanEmail, cleanMatKhau)
            authResult.onSuccess { user ->
                val userDetailsResult = authRepo.saveUserDetails(
                    userId = user.uid,
                    hoTen = cleanHoTen,
                    email = cleanEmail,
                    ngaySinh = ngaySinh
                )
                userDetailsResult.onSuccess {
                    _uiState.value = RegisterUiState(registrationResult = "Đăng ký thành công!")
                }
                userDetailsResult.onFailure {
                    _uiState.value = RegisterUiState(registrationResult = "Tạo nick thành công, nhưng lưu dữ liệu thất bại.")
                }
            }
            authResult.onFailure { exception ->
                _uiState.value = RegisterUiState(registrationResult = "Đăng ký thất bại: ${exception.message}")
            }
        }
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 8) {
            return "Mật khẩu phải có ít nhất 8 ký tự."
        }
        if (!password[0].isUpperCase()) {
            return "Mật khẩu phải bắt đầu bằng chữ viết hoa."
        }
        val specialCharRegex = Regex("[^A-Za-z0-9]")
        if (!specialCharRegex.containsMatchIn(password)) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt."
        }
        return null
    }
    fun resetRegistrationResult() {
        _uiState.value = _uiState.value.copy(registrationResult = null)
    }

    fun registerWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState(isLoading = true)

            val result = authRepo.signInWithGoogle(idToken)

            result.onSuccess { user ->
                val userId = user.uid
                val email = user.email ?: ""
                val name = user.displayName ?: "Người dùng Google"
                val saveResult = authRepo.saveUserDetails(
                    userId = userId,
                    hoTen = name,
                    email = email,
                    ngaySinh = ""
                )

                saveResult.onSuccess {
                    _uiState.value = RegisterUiState(registrationResult = "Đăng ký thành công!")
                }
                saveResult.onFailure {
                    // Nếu lưu DB thất bại thì vẫn coi như đăng ký thành công (vì Auth đã xong)
                    _uiState.value = RegisterUiState(registrationResult = "Đăng ký thành công!")
                }
            }

            result.onFailure { e ->
                _uiState.value = RegisterUiState(registrationResult = "Lỗi Google: ${e.message}")
            }
        }
    }
}