package com.nhom10.quanlybanhang.ui.screens.payment // Nằm chung gói payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    navController: NavController,
    khachTra: String, // Tham số nhận từ PaymentScreen
    tienThua: String  // Tham số nhận từ PaymentScreen
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Nền xám nhạt

    // Biến state cho hộp thoại
    var showProDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = scaffoldBgColor, // Nền xám nhạt cho toàn màn hình

        // === 1. TOP BAR (ĐÃ SỬA) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Hóa đơn", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Nút quay lại
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                // Bỏ `actions` (nút Sửa) ở đây
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        // === 2. BOTTOM BAR (ĐÃ THÊM LẠI) ===
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White, // Nền trắng
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nút Xong
                    Button(
                        onClick = {
                            // Quay về màn hình Home
                            navController.popBackStack(Routes.HOME, inclusive = false)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray.copy(alpha = 0.7f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Xong", color = Color.Black, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    // Nút In hóa đơn
                    Button(
                        onClick = { showProDialog = true }, // Mở hộp thoại
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("In hóa đơn", modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        },

        // === 3. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues) // Padding từ Scaffold
                    .verticalScroll(rememberScrollState())
            ) {
                // --- Khối 1: Thông tin chung & Sản phẩm (Dữ liệu cứng) ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 16.dp) // Thêm padding để không dính sát TopBar
                ) {
                    InvoiceInfoSection()
                    Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                    ProductListSection()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Khối 2: Tóm tắt thanh toán (Dữ liệu động) ---
                InvoiceSummarySection(
                    khachTra = khachTra,
                    tienThua = tienThua
                )
            }

            // === HỘP THOẠI NÂNG CẤP PRO ===
            if (showProDialog) {
                AlertDialog(
                    onDismissRequest = { showProDialog = false }, // Sửa lỗi
                    title = { Text("Thông báo") },
                    text = { Text("Bạn hãy nâng cấp ứng dụng Pro để có thể in hóa đơn.") },
                    confirmButton = {
                        TextButton(
                            onClick = { showProDialog = false }
                        ) {
                            Text("Đã hiểu")
                        }
                    }
                )
            }
        }
    )
}

// (Tất cả các Composable phụ trợ )

@Composable
private fun InvoiceInfoSection() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp), // Chỉ padding ngang
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Đơn hàng", value = "DH.3636")
        InfoRow(label = "Thời gian", value = "02/10/2025 23:00")
        InfoRow(label = "Khách hàng", value = "Tú")
    }
}

@Composable
private fun ProductListSection() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp), // Chỉ padding ngang
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProductItem(title = "Tôm", subtitle = "100.000 x 1kg", total = "100.000")
        ProductItem(title = "Cá", subtitle = "50.000 x 1kg", total = "50.000")
        ProductItem(title = "Cua", subtitle = "150.000 x 1kg", total = "150.000")
    }
}

@Composable
private fun InvoiceSummarySection(
    khachTra: String,
    tienThua: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp) // Để cách lề
            .background(Color.White)
            .padding(16.dp), // Padding bên trong
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryRow(label = "Chiết khấu", value = "0")
        SummaryRow(label = "Thuế", value = "0")
        SummaryRow(label = "Khách trả", value = khachTra) // <-- Dữ liệu động
        SummaryRow(label = "Tiền thừa", value = tienThua) // <-- Dữ liệu động
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SummaryTotalRow(label = "Tổng tiền", total = "300.000") // Dữ liệu cứng
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProductItem(title: String, subtitle: String, total: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Text(
            text = total,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryTotalRow(label: String, total: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = total,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun InvoiceScreenPreview() {
    InvoiceScreen(
        navController = rememberNavController(),
        khachTra = "600.000",
        tienThua = "300.000"
    )
}