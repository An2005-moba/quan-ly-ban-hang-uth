package com.nhom10.quanlybanhang.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.service.OrderViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    orderViewModel: OrderViewModel // Nhận ViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)

    // Lấy tổng tiền từ ViewModel
    val totalAmount by orderViewModel.totalAmount.collectAsState()

    // State nhập liệu (String để dễ xử lý thêm số 0)
    var inputString by remember { mutableStateOf("") }

    // Tính toán
    val cashGiven = inputString.toDoubleOrNull() ?: 0.0
    val remaining = totalAmount - cashGiven
    val change = cashGiven - totalAmount

    val formatter = DecimalFormat("#,###")

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thành tiền", fontWeight = FontWeight.Bold) },
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
                Button(
                    onClick = {
                        // 1. Lưu số tiền khách trả vào ViewModel
                        orderViewModel.updateCashGiven(cashGiven)
                        // 2. Chuyển sang màn hình Hóa đơn
                        navController.navigate(Routes.INVOICE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp),
                    // Chỉ cho thanh toán nếu khách trả đủ
                    enabled = remaining <= 0
                ) {
                    Text(
                        text = "Thanh toán",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                // Tab Tiền mặt / Ngân hàng (Giữ nguyên UI)
                Row(Modifier.fillMaxWidth().background(Color.White)) {
                    PaymentTab(
                        text = "Tiền Mặt",
                        isSelected = true,
                        modifier = Modifier.weight(1f), // Đặt weight ở đây
                        onClick = {}
                    )
                    PaymentTab(
                        text = "Ngân hàng",
                        isSelected = false,
                        modifier = Modifier.weight(1f), // Đặt weight ở đây
                        onClick = { navController.navigate(Routes.BANK_PAYMENT) }
                    )
                }

                // Phần hiển thị số tiền
                Column(
                    modifier = Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Khách trả:", fontSize = 16.sp)
                    Text(
                        text = if (inputString.isEmpty()) "0" else formatter.format(cashGiven),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (remaining > 0) {
                        Text("Khách thiếu: ${formatter.format(remaining)}", color = Color.Red)
                    } else {
                        Text("Tiền thừa: ${formatter.format(change)}", color = appBlueColor, fontWeight = FontWeight.Bold)
                    }
                }

                // Bàn phím số
                CalculatorPad(
                    onNumberClick = { num ->
                        if (inputString.length < 12) { // Giới hạn độ dài
                            inputString = if (inputString == "0") num else inputString + num
                        }
                    },
                    onBackspace = {
                        if (inputString.isNotEmpty()) {
                            inputString = inputString.dropLast(1)
                        }
                    },
                    onClear = { inputString = "" }
                )
            }
        }
    )
}

@Composable
fun PaymentTab(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier, // Nhận modifier từ ngoài
    onClick: () -> Unit
) {
    Column(
        modifier = modifier // Dùng modifier được truyền vào
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text,
            modifier = Modifier.padding(16.dp),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color(0xFF0088FF) else Color.Gray
        )
        if (isSelected) Divider(color = Color(0xFF0088FF), thickness = 2.dp)
    }
}

@Composable
fun CalculatorPad(onNumberClick: (String) -> Unit, onBackspace: () -> Unit, onClear: () -> Unit) {
    val buttons = listOf(
        listOf("7", "8", "9"),
        listOf("4", "5", "6"),
        listOf("1", "2", "3"),
        listOf("C", "0", "backspace")
    )

    Column(Modifier.background(Color.White).padding(8.dp)) {
        buttons.forEach { row ->
            Row(Modifier.fillMaxWidth().weight(1f, false)) {
                row.forEach { key ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.5f)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF5F5F5))
                            .clickable {
                                when (key) {
                                    "C" -> onClear()
                                    "backspace" -> onBackspace()
                                    else -> onNumberClick(key)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "backspace") {
                            Icon(Icons.Default.Backspace, contentDescription = null)
                        } else {
                            Text(key, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}