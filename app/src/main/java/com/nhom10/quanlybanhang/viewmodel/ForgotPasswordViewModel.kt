package com.nhom10.quanlybanhang.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ForgotPasswordUiState(
    val emailError: String? = null,
    val result: String? = null,
    val isLoading: Boolean = false
)

class ForgotPasswordViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun sendResetLink(email: String) {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.value = ForgotPasswordUiState(emailError = "Email không hợp lệ.")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState(isLoading = true)
            try {
                auth.sendPasswordResetEmail(cleanEmail).await()
                _uiState.value = ForgotPasswordUiState(result = "Đã gửi link, vui lòng kiểm tra email!")
            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState(result = "Lỗi: ${e.message}")
            }
        }
    }

    fun resetResultState() {
        _uiState.value = _uiState.value.copy(result = null)
    }
}