package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.AuthRepository
import com.nhom10.quanlybanhang.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        loadUserData()
    }

    init {
        auth.addAuthStateListener(authStateListener)
        loadUserData()
    }
    fun loadUserData() {
        val user = auth.currentUser
        if (user != null) {
            val authName = user.displayName ?: "Người dùng"
            val authPhoto = user.photoUrl?.toString()
            val id = "ID: ${user.uid.take(6)}..."
            _uiState.value = _uiState.value.copy(
                userName = authName,
                userId = id,
                photoUrl = authPhoto
            )
            viewModelScope.launch {
                val result = authRepo.getUserDetails(user.uid)
                result.onSuccess { data ->
                    val dbName = data["hoTen"] as? String
                    val dbPhoto = data["photoUrl"] as? String
                    _uiState.value = _uiState.value.copy(
                        userName = dbName ?: _uiState.value.userName,
                        photoUrl = dbPhoto ?: _uiState.value.photoUrl
                    )
                }
            }
        } else {
            _uiState.value = AccountUiState()
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}