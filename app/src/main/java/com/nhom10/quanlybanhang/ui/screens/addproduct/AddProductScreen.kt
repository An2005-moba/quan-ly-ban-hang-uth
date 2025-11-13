package com.nhom10.quanlybanhang.ui.screens.addproduct // Đảm bảo tên gói đúng

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Image // Icon giữ chỗ cho ảnh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.service.ProductViewModel


import android.widget.Toast

import androidx.compose.ui.platform.LocalContext // Thêm import
import com.nhom10.quanlybanhang.Routes

import com.nhom10.quanlybanhang.model.Product // Thêm import

/**
 * Màn hình Thêm mặt hàng mới
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel // Sửa: Nhận ViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current // Thêm: Để dùng Toast

    // Biến state cho các trường
    var tenMatHang by remember { mutableStateOf("") }
    var maMatHang by remember { mutableStateOf("") }
    var soLuong by remember { mutableStateOf("0") }
    var giaBan by remember { mutableStateOf("0") }
    var giaNhap by remember { mutableStateOf("0") }
    var donViTinh by remember { mutableStateOf("Kg") }
    var apDungThue by remember { mutableStateOf(true) }
    var ghiChu by remember { mutableStateOf("") }

    Scaffold(
        // === 1. TOP BAR (Có nút Quay lại và nút Lưu) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thêm mặt hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Quay lại
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    // Nút "Lưu"
                    TextButton(  onClick = { navController.navigate(Routes.HOME)

                        // 1. Chuyển đổi kiểu dữ liệu
                        val soLuongDouble = soLuong.toDoubleOrNull() ?: 0.0
                        val giaBanDouble = giaBan.replace(".", "").toDoubleOrNull() ?: 0.0
                        val giaNhapDouble = giaNhap.replace(".", "").toDoubleOrNull() ?: 0.0

                        // 2. Tạo đối tượng Product
                        val newProduct = Product(
                            tenMatHang = tenMatHang,
                            maMatHang = maMatHang,
                            soLuong = soLuongDouble,
                            giaBan = giaBanDouble,
                            giaNhap = giaNhapDouble,
                            donViTinh = donViTinh,
                            apDungThue = apDungThue,
                            ghiChu = ghiChu
                        )

                        // 3. Gọi ViewModel để lưu
                        productViewModel.addProduct(
                            product = newProduct,
                            onSuccess = {
                                // Khi thành công, quay về
                                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onFailure = { e ->
                                // Báo lỗi
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }) {
                        Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        // === 2. NỘI DUNG CHÍNH (Giữ nguyên) ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5)) // Nền xám nhạt
                    .verticalScroll(rememberScrollState()) // Cho phép cuộn
            ) {
                // --- Khối 1: Upload ảnh ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.5f))
                            .clickable { /* TODO: Mở thư viện ảnh */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Thêm ảnh",
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                        // TODO: Dùng AsyncImage của Coil để hiển thị ảnh đã chọn
                    }
                }

                // --- Khối 2: Các trường thông tin (dùng TextField) ---
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {
                    InfoTextField(label = "Tên mặt hàng", value = tenMatHang, onValueChange = { tenMatHang = it })
                    Divider()
                    InfoTextField(label = "Mã mặt hàng", value = maMatHang, onValueChange = { maMatHang = it })
                    Divider()
                    InfoTextField(label = "Số lượng", value = soLuong, onValueChange = { soLuong = it })
                    Divider()
                    InfoTextField(label = "Giá bán", value = giaBan, onValueChange = { giaBan = it })
                    Divider()
                    InfoTextField(label = "Giá nhập", value = giaNhap, onValueChange = { giaNhap = it })
                    Divider()

                    // Mục "Đơn vị tính" (có mũi tên)
                    InfoRowWithNavigation(
                        label = "Đơn vị tính",
                        value = donViTinh,
                        onClick = { /* TODO: Mở dialog chọn đơn vị */ }
                    )
                    Divider()

                    // Mục "Áp dụng thuế" (có Switch)
                    InfoRowWithSwitch(
                        label = "Áp dụng thuế",
                        checked = apDungThue,
                        onCheckedChange = { apDungThue = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Khối 3: Ghi chú ---
                TextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    placeholder = { Text("Ghi chú") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // Đặt chiều cao cho ô ghi chú
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent, // Tắt gạch chân
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    )
}

/**
 * Composable phụ trợ cho một hàng nhập liệu (TextField không viền)
 */
@Composable
private fun InfoTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

/**
 * Composable phụ trợ cho một hàng có chữ và mũi tên (như Đơn vị tính)
 */
@Composable
private fun InfoRowWithNavigation(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp), // Padding giống TextField
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, color = Color.Gray)
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
    }
}

/**
 * Composable phụ trợ cho một hàng có chữ và Switch (như Áp dụng thuế)
 */
@Composable
private fun InfoRowWithSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Padding cho Switch
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0088FF) // Màu xanh
            )
        )
    }
}


@Preview(showBackground = true)
@Composable
fun AddProductScreenPreview() {
    AddProductScreen(
        navController = rememberNavController(),
        productViewModel = TODO(),
    )
}