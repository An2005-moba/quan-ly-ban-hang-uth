package com.nhom10.quanlybanhang.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.data.model.Order
import com.nhom10.quanlybanhang.data.model.OrderItem
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel // Cần import ViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(
    navController: NavController,
    orderViewModel: OrderViewModel // ĐÃ THÊM: Truyền OrderViewModel vào
) {
    val appBlue = Color(0xFF3388FF)
    val grayBackground = Color(0xFFF0F2F5)

    // Lấy đối tượng Order từ savedStateHandle
    val order = navController.previousBackStackEntry?.savedStateHandle?.get<Order>("order")

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Không tìm thấy dữ liệu hóa đơn", color = Color.Gray)
            Button(onClick = { navController.popBackStack() }, modifier = Modifier.padding(top = 8.dp)) {
                Text("Quay lại")
            }
        }
        return
    }

    val formattedDate = remember(order.date) {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(order.date))
    }

    // Hàm xử lý khi nhấn nút Xóa
    val onDeleteClick: () -> Unit = {
        // 1. Gọi hàm xóa Order từ ViewModel
        orderViewModel.deleteOrder(order.id)

        // 2. Quay lại màn hình lịch sử sau khi xóa
        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết hóa đơn", fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.Bold) },
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
                    IconButton(onClick = onDeleteClick) { // ĐÃ SỬA: Gán hàm onDeleteClick
                        Icon(Icons.Default.Delete, contentDescription = "Xóa hóa đơn", tint = Color.White)
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
            // 1. Thông tin chung
            item {
                BillInfoCard(
                    orderId = order.id,
                    dateTime = formattedDate,
                    customer = order.customerName
                )
            }

            // 2. Danh sách sản phẩm
            item {
                Spacer(Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Text("Danh sách món", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    order.items.forEach { item ->
                        ProductDetailRow(item)
                        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }

            // 3. Tổng kết tiền
            item {
                Spacer(Modifier.height(8.dp))
                BillSummaryCard(
                    totalAmount = order.tongTien,
                    receivedAmount = order.khachTra,
                    changeAmount = order.tienThua,
                    discount = order.chietKhau,
                    surcharge = order.phuPhi,
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
            Text("Mã đơn: $orderId", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(Modifier.height(6.dp))
            Text("Thời gian: $dateTime", color = Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(6.dp))
            Text("Khách hàng: $customer", color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun ProductDetailRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.tenMatHang, fontWeight = FontWeight.Medium, fontSize = 15.sp)
            Text(
                "${item.giaBan.formatVND()} x ${item.soLuong} ${item.donViTinh}",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
        Text(
            (item.giaBan * item.soLuong).formatVND(),
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

@Composable
fun BillSummaryCard(
    totalAmount: Double,
    receivedAmount: Double,
    changeAmount: Double,
    discount: Double,
    surcharge: Double,
    appBlue: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            // [ĐÃ SỬA] Tách padding horizontal và bottom ra
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (discount > 0) {
                SummaryRow("Chiết khấu", "$discount%")
            }
            if (surcharge > 0) {
                SummaryRow("Phụ phí", surcharge.formatVND())
            }

            SummaryRow("Khách trả", receivedAmount.formatVND())
            SummaryRow("Tiền thừa", changeAmount.formatVND())

            HorizontalDivider(Modifier.padding(vertical = 12.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TỔNG TIỀN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(totalAmount.formatVND(), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = appBlue)
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

fun Double.formatVND(): String = "${"%,.0f".format(this).replace(",", ".")}₫"