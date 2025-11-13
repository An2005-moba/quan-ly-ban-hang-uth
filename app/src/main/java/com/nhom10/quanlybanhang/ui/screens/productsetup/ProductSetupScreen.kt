package com.nhom10.quanlybanhang.ui.screens.productsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.model.ProductItem
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSetupScreen(navController: NavController) {
    val appBlueColor = Color(0xFF0088FF)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Danh sách mẫu
    val sampleProducts = listOf(
        ProductItem(id = "1", name = "Điện thoại", price = 5000.0, quantity = 100, unit = "Cái"),
        ProductItem(id = "2", name = "Laptop", price = 20000000.0, quantity = 98, unit = "Cái"),
        ProductItem(id = "3", name = "Đồng hồ", price = 1000000.0, quantity = 95, unit = "Cái"),
        ProductItem(id = "4", name = "Cá", price = 100000.0, quantity = 3, unit = "Kg")
    )




    // Map tên sản phẩm -> icon
    fun getProductIcon(name: String): ImageVector = when {
        name.contains("Điện thoại") -> Icons.Default.Smartphone
        name.contains("Laptop") -> Icons.Default.Laptop
        name.contains("Đồng hồ") -> Icons.Default.Watch
        name.contains("Cá") -> Icons.Default.Fastfood
        else -> Icons.Default.Add
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thiết lập mặt hàng", fontWeight = FontWeight.Bold) },
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
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(sampleProducts) { product ->
                        ProductListItem(
                            icon = getProductIcon(product.name),
                            title = product.name,
                            details = "Giá: ${product.price} - Còn: ${product.quantity} ${product.unit}",
                            onClick = { navController.currentBackStackEntry?.savedStateHandle?.set("product", product)
                                navController.navigate(Routes.EDIT_PRODUCT)
                            }
                        )
                    }
                }
            }
        }
    )
}

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
            Icon(icon, contentDescription = null, modifier = Modifier.padding(start = 8.dp))
        },
        headlineContent = { Text(title, fontWeight = FontWeight.Bold) },
        supportingContent = { Text(details) },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Preview(showBackground = true)
@Composable
fun ProductSetupScreenPreview() {
    ProductSetupScreen(navController = rememberNavController())
}
