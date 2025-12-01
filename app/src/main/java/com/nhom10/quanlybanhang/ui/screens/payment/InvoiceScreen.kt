package com.nhom10.quanlybanhang.ui.screens.payment

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    navController: NavController,
    orderViewModel: OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current

    // Lấy dữ liệu từ ViewModel
    val cartItems by orderViewModel.cartItems.collectAsState()
    val selectedCustomer by orderViewModel.selectedCustomer.collectAsState()
    val totalAmount by orderViewModel.totalAmount.collectAsState()
    val cashGiven by orderViewModel.cashGiven.collectAsState()
    val changeAmount by orderViewModel.changeAmount.collectAsState()
    val discountPercent by orderViewModel.discountPercent.collectAsState()
    val surcharge by orderViewModel.surcharge.collectAsState()
    val currentOrderId by orderViewModel.currentOrderId.collectAsState()

    val formatter = DecimalFormat("#,###")
    val dateNow = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    val isLoading by orderViewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF0F2F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hóa đơn", fontWeight = FontWeight.Bold) },
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
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {
                Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

                    // --- NÚT XONG (Đã sửa đổi) ---
                    Button(
                        onClick = {
                            orderViewModel.saveOrderToFirebase(
                                onSuccess = {
                                    Toast.makeText(context, "Đã lưu hóa đơn!", Toast.LENGTH_SHORT).show()
                                    navController.navigate(Routes.HOME) { popUpTo(0) }
                                },
                                onFailure = { e ->
                                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(8.dp),

                        // 3. Vô hiệu hóa nút khi đang tải để tránh bấm nhiều lần
                        enabled = !isLoading
                    ) {
                        // 4. Kiểm tra: Nếu đang tải thì hiện vòng xoay, ngược lại hiện chữ "Xong"
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = appBlueColor,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Xong", color = Color.Black)
                        }
                    }

                    // Nút In hóa đơn (Giả lập)
                    Button(
                        onClick = { /* Logic in ấn */ },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("In hóa đơn")
                    }
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(Modifier.background(Color.White).padding(16.dp)) {
                    // Thông tin chung
                    InfoRow("Đơn hàng", currentOrderId)
                    InfoRow("Thời gian", dateNow)
                    InfoRow("Khách hàng", selectedCustomer?.tenKhachHang ?: "Khách lẻ")

                    Divider(Modifier.padding(vertical = 16.dp))

                    // Danh sách món
                    cartItems.forEach { item ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(item.tenMatHang, fontWeight = FontWeight.Bold)
                                Text("${formatter.format(item.giaBan)} x ${item.soLuong} ${item.donViTinh}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                            Text(formatter.format(item.giaBan * item.soLuong))
                        }
                    }

                    Divider(Modifier.padding(vertical = 16.dp))

                    // --- TÍNH TOÁN ---
                    val itemsTotal = cartItems.sumOf { it.giaBan * it.soLuong }
                    val discountAmount = itemsTotal * (discountPercent / 100)

                    // Tổng kết
                    InfoRow("Chiết khấu (${discountPercent}%)", formatter.format(discountAmount))
                    InfoRow("Phụ phí", formatter.format(surcharge))
                    InfoRow("Thuế", "0")

                    Divider(Modifier.padding(vertical = 8.dp))

                    InfoRow("Khách trả", formatter.format(cashGiven))
                    InfoRow("Tiền thừa", formatter.format(changeAmount))

                    Divider(Modifier.padding(vertical = 16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Tổng tiền", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(formatter.format(totalAmount), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Text(value, textAlign = TextAlign.End)
    }
}