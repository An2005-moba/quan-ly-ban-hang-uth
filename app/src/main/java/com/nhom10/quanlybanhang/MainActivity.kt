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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.ui.theme.QuanLyBanHangTheme
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel
import com.nhom10.quanlybanhang.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- LOGIC GHI NHỚ ĐĂNG NHẬP ---
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        val startDestination = if (currentUser != null) {
            Routes.HOME
        } else {
            Routes.LOGIN
        }
        // -------------------------------

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val fontSizeViewModel: FontSizeViewModel = viewModel()

            val themeMode by themeViewModel.themeMode.collectAsState()
            val fontSize by fontSizeViewModel.fontSize.collectAsState()

            val darkTheme = when (themeMode) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }

            // Tạo Typography động theo fontSize
            val appTypography = MaterialTheme.typography.copy(
                bodyLarge = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize.sp),
                bodyMedium = MaterialTheme.typography.bodyMedium.copy(fontSize = fontSize.sp),
                titleLarge = MaterialTheme.typography.titleLarge.copy(fontSize = fontSize.sp),
                titleMedium = MaterialTheme.typography.titleMedium.copy(fontSize = fontSize.sp)
            )

            QuanLyBanHangTheme(
                darkTheme = darkTheme,
                fontSizeViewModel = fontSizeViewModel
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        startDestination = startDestination,
                        fontSizeViewModel = fontSizeViewModel
                    )
                }
            }
        }
    }
}
