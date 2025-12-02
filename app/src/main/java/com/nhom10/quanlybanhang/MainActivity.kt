package com.nhom10.quanlybanhang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel // Import
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel // Import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) Routes.HOME else Routes.LOGIN

        setContent {
            // 1. Khởi tạo ViewModel Cỡ chữ
            val fontSizeViewModel: FontSizeViewModel = viewModel()

            // 2. Truyền vào Theme để áp dụng toàn app (nếu Theme bạn hỗ trợ)
            QuanLyBanHangTheme(
                fontSizeViewModel = fontSizeViewModel
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Truyền vào AppNavigation
                    AppNavigation(
                        startDestination = startDestination,
                        fontSizeViewModel = fontSizeViewModel // <-- Truyền ở đây
                    )
                }
            }
        }
    }
}