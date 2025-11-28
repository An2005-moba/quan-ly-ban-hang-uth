package com.nhom10.quanlybanhang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme

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
            QuanLyBanHangTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Truyền điểm bắt đầu vào AppNavigation
                    AppNavigation(startDestination = startDestination)
                }
            }
        }
    }
}