package com.nhom10.quanlybanhang

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel // Import
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel // Import
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    println("Đăng ký thất bại")
                } else {
                    println("Đăng ký thành công topic all_users")
                }
            }


        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val startDestination = if (currentUser != null) Routes.HOME else Routes.LOGIN

        setContent {
            // 1. Khởi tạo ViewModel Cỡ chữ
            val fontSizeViewModel: FontSizeViewModel = viewModel()
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        Log.d("FCM", "Người dùng đã cấp quyền thông báo")
                    } else {
                        Log.d("FCM", "Người dùng từ chối quyền thông báo")
                    }
                }
            )

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