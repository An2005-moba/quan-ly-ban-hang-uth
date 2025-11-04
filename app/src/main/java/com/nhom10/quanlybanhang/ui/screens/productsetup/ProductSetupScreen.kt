package com.nhom10.quanlybanhang.ui.screens.productsetup // Đảm bảo tên gói đúng

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// Import các icon bạn cần
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material.icons.filled.Fastfood // Dùng icon này cho "Cá"
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSetupScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Dữ liệu mẫu cho danh sách
    val sampleProducts = listOf(
        ProductItem(Icons.Default.Smartphone, "Điện thoại", "Giá: 5.000 - Còn: 100 cái"),
        ProductItem(Icons.Default.Laptop, "Laptop", "Giá: 20.000.000 - Còn: 98 cái"),
        ProductItem(Icons.Default.Watch, "Đồng hồ", "Giá: 1.000.000 - Còn: 95 cái"),
        ProductItem(Icons.Default.Fastfood, "Cá", "Giá: 100.000 - Còn: 3kg") // Dùng icon giữ chỗ
    )

    Scaffold(
        // === 1. TOP BAR (Có nút Quay lại và nút Cộng) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thiết lập mặt hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.ADD_PRODUCT) }) {
                        Icon(Icons.Default.Add, "Thêm mặt hàng")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Tìm kiếm mặt hàng") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = lightGrayBorder,
                        focusedBorderColor = appBlueColor
                    )
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp) // Ngăn cách mỏng
                ) {
                    items(sampleProducts) { product ->
                        ProductListItem(
                            icon = product.icon,
                            title = product.title,
                            details = product.details,
                            onClick = { /* TODO: Mở trang chi tiết mặt hàng */ }
                        )
                    }
                }
            }
        }
    )
}

// Data class để chứa thông tin item
private data class ProductItem(val icon: ImageVector, val title: String, val details: String)

/**
 * Composable phụ trợ cho một item trong danh sách
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListItem(
    icon: ImageVector,
    title: String,
    details: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        },
        headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(details) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent) // Nền trong suốt
    )
}

@Preview(showBackground = true)
@Composable
fun ProductSetupScreenPreview() {
    ProductSetupScreen(navController = rememberNavController())
}