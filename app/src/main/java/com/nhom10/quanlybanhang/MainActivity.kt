package com.nhom10.quanlybanhang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme
import com.nhom10.quanlybanhang.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- LOGIC GHI NHỚ ĐĂNG NHẬP ---
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // Nếu user khác null (đã đăng nhập) -> Vào HOME
        // Nếu user là null (chưa đăng nhập) -> Vào LOGIN
        val startDestination = if (currentUser != null) {
            Routes.HOME
        } else {
            Routes.LOGIN
        }
        // -------------------------------

        setContent {
            // 1. Gọi ViewModel
            val themeViewModel: ThemeViewModel = viewModel()

            // 2. Lắng nghe chế độ (0, 1, 2)
            val themeMode by themeViewModel.themeMode.collectAsState()

            // 3. Tính toán xem có Dark Mode không
            val darkTheme = when (themeMode) {
                1 -> false // Luôn Sáng
                2 -> true  // Luôn Tối
                else -> isSystemInDarkTheme() // Theo điện thoại
            }

            // 4. Áp dụng vào Theme
            QuanLyBanHangTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}