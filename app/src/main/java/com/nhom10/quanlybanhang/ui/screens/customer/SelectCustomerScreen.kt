package com.nhom10.quanlybanhang.ui.screens.customer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.nhom10.quanlybanhang.Routes

// Data class mẫu
data class Customer(
    val id: Int,
    val name: String,
    val phone: String?,
    val avatarUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCustomerScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Màu nền xám nhạt
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Dữ liệu mẫu
    val customers = listOf(
        Customer(0, "Khác lẻ", null),
        Customer(1, "Nguyễn Văn Bóp", "0999999", "URL_AVATAR_BOP") // Thay URL thật
    )
    var selectedCustomerId by remember { mutableStateOf(0) } // "Khác lẻ" được chọn mặc định

    Scaffold(
        containerColor = scaffoldBgColor,
        // === 1. TOP BAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chọn khách hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.ADD_CUSTOMER) }) {
                        Icon(Icons.Default.Add, "Thêm khách hàng")
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
                    placeholder = { Text("Nhập tên, số điện thoại") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = lightGrayBorder,
                        focusedBorderColor = appBlueColor
                    )
                )

                // --- Danh sách khách hàng ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp) // Ngăn cách mỏng
                ) {
                    items(customers) { customer ->
                        CustomerListItem(
                            customer = customer,
                            isSelected = customer.id == selectedCustomerId,
                            onClick = { selectedCustomerId = customer.id }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun CustomerListItem(
    customer: Customer,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val appBlueColor = Color(0xFF0088FF)
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)

    ListItem(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        headlineContent = { Text(customer.name) },
        supportingContent = {
            if (customer.phone != null) {
                Text(customer.phone)
            }
        },
        leadingContent = {
            if (customer.avatarUrl != null) {
                AsyncImage(
                    model = customer.avatarUrl,
                    contentDescription = customer.name,
                    placeholder = placeholderPainter,
                    error = placeholderPainter,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Icon Person mặc định cho "Khác lẻ"
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        trailingContent = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Đã chọn",
                    tint = appBlueColor
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    Divider() // Thêm đường kẻ
}


@Preview(showBackground = true)
@Composable
fun SelectCustomerScreenPreview() {
    SelectCustomerScreen(navController = rememberNavController())
}