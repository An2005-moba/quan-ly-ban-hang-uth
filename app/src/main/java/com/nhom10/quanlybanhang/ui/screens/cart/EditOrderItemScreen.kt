package com.nhom10.quanlybanhang.ui.screens.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderItemScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    val deleteButtonColor = Color(0xFFD9534F)
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Biến state (dữ liệu mẫu)
    var tenMatHang by remember { mutableStateOf("Tôm") }
    var soLuong by remember { mutableStateOf(1) }
    var giaBan by remember { mutableStateOf("100.000") }
    var chietKhau by remember { mutableStateOf("0 %") }
    var ghiChu by remember { mutableStateOf("") }

    Scaffold(
        containerColor = scaffoldBgColor,
        // === 1. TOP BAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chỉnh Sửa chi tiết", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
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
        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // --- Tên mặt hàng ---
                Text(
                    text = tenMatHang,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                // --- Bộ đếm số lượng ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    IconButton(
                        onClick = { if (soLuong > 1) soLuong-- },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Giảm", modifier = Modifier.fillMaxSize())
                    }
                    Text(
                        text = soLuong.toString(),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { soLuong++ },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.Default.Add, "Tăng", modifier = Modifier.fillMaxSize())
                    }
                }

                // --- Các trường thông tin ---
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    InfoTextField(
                        value = giaBan,
                        onValueChange = { giaBan = it },
                        label = "Giá bán",
                        enabled = true, // <-- SỬA Ở ĐÂY
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // <-- THÊM
                    )
                    InfoTextField(
                        value = chietKhau,
                        onValueChange = { chietKhau = it },
                        label = "Chiết khấu mặt hàng",
                        enabled = true, // <-- SỬA Ở ĐÂY
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // <-- THÊM
                    )
                    InfoTextField(
                        value = ghiChu,
                        onValueChange = { ghiChu = it },
                        label = "Ghi Chú",
                        enabled = true,
                        modifier = Modifier.height(150.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // --- Nút Xóa ---
                Button(
                    onClick = { /* TODO: Xử lý xóa item */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = deleteButtonColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Xóa", fontSize = 18.sp)
                }
            }
        }
    )
}

@Composable
private fun InfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            unfocusedBorderColor = Color.Black.copy(alpha = 0.2f),
            disabledBorderColor = Color.Black.copy(alpha = 0.2f),
            disabledTextColor = Color.Black,
            disabledLabelColor = Color.Black.copy(alpha = 0.5f)
        ),
        singleLine = modifier != Modifier.height(150.dp),
        keyboardOptions = keyboardOptions
    )
}


@Preview(showBackground = true)
@Composable
fun EditOrderItemScreenPreview() {
    EditOrderItemScreen(navController = rememberNavController())
}