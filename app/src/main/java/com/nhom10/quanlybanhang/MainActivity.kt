package com.nhom10.quanlybanhang // (Nhớ đổi lại cho đúng tên gói của bạn)

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
// Import màn hình LoginScreen bạn vừa tạo
import com.nhom10.quanlybanhang.ui.screens.auth.LoginScreen
import com.nhom10.quanlybanhang.ui.screens.auth.RegisterScreen
import com.nhom10.quanlybanhang.ui.screens.home.HomeScreen
// Import theme của bạn
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme // (Tên theme này có thể khác)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuanLyBanHangTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeScreen()
                }
            }
        }
    }
}