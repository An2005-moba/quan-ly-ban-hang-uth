package com.nhom10.quanlybanhang.ui.screens.productsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// Import các icon bạn cần
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood // Dùng icon này cho "Cá"
import androidx.compose.material.icons.filled.Search
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
import com.nhom10.quanlybanhang.service.ProductViewModel // Thêm import

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSetupScreen(
    navController: NavController,
    productViewModel: ProductViewModel // Sửa: Nhận ViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Sửa: Lấy danh sách từ ViewModel
    val productList by productViewModel.products.collectAsState()

    // Sửa: Tải dữ liệu khi màn hình khởi chạy
    LaunchedEffect(key1 = true) {
        productViewModel.loadProducts()
    }

    // Xóa: Dữ liệu mẫu
    // val sampleProducts = listOf(...)

    Scaffold(
        // === 1. TOP BAR (Giữ nguyên) ===
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
                // Thanh tìm kiếm (Giữ nguyên)
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

                // Sửa: Dùng productList từ ViewModel
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp) // Ngăn cách mỏng
                ) {
                    // Lọc danh sách dựa trên tìm kiếm (ví dụ)
                    val filteredList = productList.filter {
                        it.tenMatHang.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredList) { product ->
                        val details = "Giá: ${product.giaBan} - Còn: ${product.soLuong} ${product.donViTinh}"

                        ProductListItem(
                            icon = Icons.Default.Fastfood, // Tạm dùng 1 icon
                            title = product.tenMatHang,
                            details = details,
                            onClick = { /* TODO: Mở trang chi tiết mặt hàng */ }
                        )
                    }
                }
            }
        }
    )
}

// Xóa: Data class ProductItem (vì đã dùng Product model)

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
    // ProductSetupScreen(navController = rememberNavController(), ...)
}