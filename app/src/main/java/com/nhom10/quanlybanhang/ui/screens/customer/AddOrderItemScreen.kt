package com.nhom10.quanlybanhang.ui.screens.customer

// --- THÊM CÁC IMPORT NÀY ---
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
// -----------------------------

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Import tất cả icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

// XÓA: Data class mẫu (sẽ dùng model/Product.kt)
// private data class ProductItem(...)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderItemScreen(
    navController: NavController,
    productViewModel: ProductViewModel, // THÊM: Nhận ProductViewModel
    orderViewModel: OrderViewModel      // THÊM: Nhận OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // SỬA: Lấy dữ liệu từ ViewModel
    val productList by productViewModel.products.collectAsState()
    val cartItems by orderViewModel.cartItems.collectAsState()

    // XÓA: Dữ liệu mẫu
    // val sampleProducts = listOf(...)

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
                            // SỬA: Badge động
                            val count = cartItems.sumOf { it.soLuong }
                            if (count > 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) { Text(count.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Giỏ hàng",
                            tint = Color.White
                        )
                    }
                    // Nút ""
                    TextButton(onClick = { navController.popBackStack() }) { // Nhấn Hủy để quay lại
                        Text("Lưu", color = Color.White)
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
                    // SỬA: Dùng productList từ ViewModel
                    val filteredList = productList.filter {
                        it.tenMatHang.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredList) { product ->
                        val details = "Giá: ${product.giaBan} - Còn: ${product.soLuong} ${product.donViTinh}"

                        ProductListItem(
                            icon = Icons.Default.Fastfood, // Tạm dùng 1 icon
                            title = product.tenMatHang,
                            details = details,
                            onClick = {
                                // SỬA: Xử lý thêm mặt hàng vào giỏ
                                orderViewModel.addProductToCart(product)
                            }
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

