package com.nhom10.quanlybanhang.ui.screens.customer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import tất cả icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

// Data class để chứa thông tin item (Giống hệt ProductSetupScreen)
private data class ProductItem(val icon: ImageVector, val title: String, val details: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderItemScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Dữ liệu mẫu (Giống ProductSetupScreen)
    val sampleProducts = listOf(
        ProductItem(Icons.Default.Smartphone, "Điện thoại", "Giá: 5.000 - Còn: 100 cái"),
        ProductItem(Icons.Default.Laptop, "Laptop", "Giá: 20.000.000 - Còn: 98 cái"),
        ProductItem(Icons.Default.Watch, "Đồng hồ", "Giá: 1.000.000 - Còn: 95 cái")
    )

    Scaffold(
        containerColor = scaffoldBgColor,
        // === 1. TOP BAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thêm vào đơn hàng", fontWeight = FontWeight.Bold)
                },
                // Lưu ý: Không có nút quay lại (navigationIcon) theo thiết kế
                actions = {
                    // Icon Giỏ hàng với Badge
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) { Text("2") }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Giỏ hàng",
                            tint = Color.White
                        )
                    }
                    // Nút "Hủy"
                    TextButton(onClick = { navController.popBackStack() }) { // Nhấn Hủy để quay lại
                        Text("Hủy", color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },

        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- Thanh tìm kiếm ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Tìm kiếm mặt hàng") }, // Placeholder mới
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = lightGrayBorder,
                        focusedBorderColor = appBlueColor
                    )
                )

                // --- Danh sách mặt hàng ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(sampleProducts) { product ->
                        ProductListItem(
                            icon = product.icon,
                            title = product.title,
                            details = product.details,
                            onClick = { /* TODO: Xử lý thêm mặt hàng này vào giỏ */ }
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable phụ trợ cho một item trong danh sách
 * (Giống hệt ProductSetupScreen)
 */
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
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    Divider() // Thêm đường kẻ
}

@Preview(showBackground = true)
@Composable
fun AddOrderItemScreenPreview() {
    AddOrderItemScreen(navController = rememberNavController())
}