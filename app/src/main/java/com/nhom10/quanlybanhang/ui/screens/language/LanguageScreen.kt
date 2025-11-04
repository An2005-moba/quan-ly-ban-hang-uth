package com.nhom10.quanlybanhang.ui.screens.language // Đảm bảo tên gói đúng

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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

/**
 * Màn hình Ngôn Ngữ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)

    Scaffold(
        // === 1. TOP BAR (CÓ NÚT QUAY LẠI) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Ngôn Ngữ", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Nhấn để quay lại
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

        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5)) // Màu nền xám nhạt
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa các mục
            ) {
                // Danh sách các ngôn ngữ (y như trong ảnh)
                LanguageOptionItem(text = "Tiếng Việt", onClick = { /* TODO: Xử lý chọn Tiếng Việt */ })
                LanguageOptionItem(text = "Tiếng Anh", onClick = { /* TODO: Xử lý chọn Tiếng Anh */ })
                LanguageOptionItem(text = "Tiếng Trung Quốc", onClick = { /* TODO: Xử lý chọn Tiếng TQ */ })
                LanguageOptionItem(text = "Tiếng Tây Ban Nha", onClick = { /* TODO: Xử lý chọn Tiếng TBN */ })
            }
        }
    )
}

/**
 * Composable phụ trợ cho một mục Ngôn ngữ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageOptionItem(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
                // Lưu ý: Không có mũi tên ở cuối
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageScreenPreview() {
    LanguageScreen(navController = rememberNavController())
}