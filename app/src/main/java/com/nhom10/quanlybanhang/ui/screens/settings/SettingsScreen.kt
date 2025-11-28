package com.nhom10.quanlybanhang.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    // Inject ViewModel để kiểm tra loại tài khoản
    viewModel: SettingsViewModel = viewModel()
) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current

    // Lấy trạng thái (isGoogleLogin) từ ViewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Cài đặt", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- MỤC MẬT KHẨU (CÓ LOGIC CHẶN GOOGLE) ---
                SettingsOptionItem(
                    text = "Mật khẩu",
                    onClick = {
                        if (uiState.isGoogleLogin) {
                            // Nếu là Google -> Báo lỗi
                            Toast.makeText(context, "Tài khoản Google không cần đổi mật khẩu", Toast.LENGTH_SHORT).show()
                        } else {
                            // Nếu là Thủ công -> Cho vào trang Đổi mật khẩu
                            navController.navigate(Routes.PASSWORD)
                        }
                    },
                    // Ẩn mũi tên nếu là Google để người dùng biết là không bấm được (Tùy chọn)
                    showArrow = !uiState.isGoogleLogin
                )

                // --- MỤC NGÔN NGỮ (LUÔN CHO PHÉP) ---
                SettingsOptionItem(
                    text = "Ngôn Ngữ",
                    onClick = { navController.navigate(Routes.LANGUAGE) },
                    showArrow = true
                )
            }
        }
    )
}

/**
 * Composable phụ trợ cho một mục Cài đặt
 * Đã sửa để các ô có chiều cao bằng nhau (60dp)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsOptionItem(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = true // Thêm tùy chọn ẩn/hiện mũi tên
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp) // Chiều cao cố định cho đều đẹp
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(navController = rememberNavController())
}