package com.nhom10.quanlybanhang.ui.screens.password // Đảm bảo tên gói đúng

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// --- THÊM IMPORT NÀY ---
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes

/**
 * Màn hình Mật khẩu (chỉ có 1 nút)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordScreen(
    navController: NavController // <-- SỬA LẠI: Nhận NavController
) {
    val appBlueColor = Color(0xFF0088FF)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mật khẩu", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    // SỬA LẠI: Dùng navController để quay lại
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
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
                // Nút "Đổi mật khẩu"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // SỬA LẠI: Dùng navController để "đi tới"
                            navController.navigate(Routes.CHANGE_PASSWORD)
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center // Căn giữa
                    ) {
                        Text(
                            text = "Đổi mật khẩu",
                            color = Color.Black.copy(alpha = 0.5f), // 50% mờ
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PasswordScreenPreview() {
    PasswordScreen(navController = rememberNavController())
}