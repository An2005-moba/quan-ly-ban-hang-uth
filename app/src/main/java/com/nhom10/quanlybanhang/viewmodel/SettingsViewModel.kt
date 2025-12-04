package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class SettingsUiState(
    val isGoogleLogin: Boolean = false
)

class SettingsViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        checkLoginProvider()
    }

    private fun checkLoginProvider() {
        val user = auth.currentUser
        if (user != null) {
            val isGoogle = user.providerData.any {
                it.providerId == GoogleAuthProvider.PROVIDER_ID
            }
            _uiState.value = SettingsUiState(isGoogleLogin = isGoogle)
        }
    }
}