package com.nhom10.quanlybanhang.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.model.ProductItem
import com.nhom10.quanlybanhang.model.TransactionWithProducts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(navController: NavController) {
    val appBlue = Color(0xFF3388FF)
    val grayBackground = Color(0xFFF0F2F5)

    // Lấy dữ liệu giao dịch từ màn History
    val transaction = navController.previousBackStackEntry?.savedStateHandle?.get<TransactionWithProducts>("transaction")

    if (transaction == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy hóa đơn", color = Color.Gray)
        }
        return
    }

    val changeAmount = transaction.amount - transaction.productList.sumOf { it.price * it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết hóa đơn", fontSize = 18.sp, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = appBlue),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Xóa hóa đơn */ }) {
                        Icon(Icons.Default.Delete, contentDescription = "Xóa", tint = Color.White)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(grayBackground)
                .padding(paddingValues)
        ) {
            item {
                BillInfoCard(
                    orderId = transaction.transactionId,
                    dateTime = "${transaction.date} ${transaction.time}",
                    customer = transaction.customerName
                )
            }

            item {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    transaction.productList.forEach { ProductDetailRow(it) }
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                BillSummaryCard(
                    totalAmount = transaction.amount.formatVND(),
                    receivedAmount = transaction.amount.formatVND(),
                    changeAmount = changeAmount.formatVND(),
                    appBlue = appBlue
                )
            }
        }
    }
}

@Composable
fun BillInfoCard(orderId: String, dateTime: String, customer: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Đơn hàng: $orderId", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text("Ngày: $dateTime", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Text("Khách hàng: $customer", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProductDetailRow(product: ProductItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(product.name, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Text("x${product.quantity} ${product.unit}", color = Color.Gray, fontSize = 13.sp)
        }
        Text((product.price * product.quantity).formatVND(), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun BillSummaryCard(
    totalAmount: String,
    receivedAmount: String,
    changeAmount: String,
    appBlue: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tổng tiền:", color = Color.Gray)
                Text(totalAmount, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Khách trả:", color = Color.Gray)
                Text(receivedAmount, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tiền thừa:", color = appBlue)
                Text(changeAmount, fontWeight = FontWeight.Bold, color = appBlue)
            }
        }
    }
}

fun Double.formatVND(): String = "${"%,.0f".format(this).replace(",", ".")}₫"
