package com.nhom10.quanlybanhang.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.model.Order
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    orderViewModel: com.nhom10.quanlybanhang.service.OrderViewModel
) {
    val appBlue = Color(0xFF3388FF)
    val grayBackground = Color(0xFFF0F2F5)

    // Ngày filter mặc định: rộng để hiện tất cả lúc đầu
    var fromDate by remember { mutableStateOf("01/01/2000") }
    var toDate by remember { mutableStateOf("31/12/2100") }

    // Lấy danh sách từ Firebase
    val orderHistory by orderViewModel.orderHistory.collectAsState()

    // 1. Lọc danh sách theo ngày (Filter)
    val filteredOrders by remember(fromDate, toDate, orderHistory) {
        derivedStateOf {
            val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = try { dateFormatter.parse(fromDate)?.time ?: 0L } catch (e: Exception) { 0L }
            val end = try { (dateFormatter.parse(toDate)?.time ?: 0L) + 86399999 } catch (e: Exception) { Long.MAX_VALUE }

            orderHistory.filter { order ->
                order.date in start..end
            }.sortedByDescending { it.date }
        }
    }

    // 2. Gom nhóm danh sách theo chuỗi ngày hiển thị (Grouping)
    // Map<String, List<Order>>: Ví dụ "20/11/2024" -> [Order1, Order2]
    val groupedOrders by remember(filteredOrders) {
        derivedStateOf {
            filteredOrders.groupBy { order ->
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(order.date))
            }
        }
    }

    // Load dữ liệu khi mở màn hình
    LaunchedEffect(Unit) {
        orderViewModel.loadOrderHistory()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(grayBackground)
                .padding(paddingValues)
        ) {
            // --- Filter Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = appBlue),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Truy vấn giao dịch", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DateInputBox("Từ ngày", fromDate, Modifier.weight(1f), appBlue) { fromDate = it }
                        DateInputBox("Đến ngày", toDate, Modifier.weight(1f), appBlue) { toDate = it }
                    }
                    Spacer(Modifier.height(16.dp))
                    // Nút hiển thị cho đẹp (chức năng lọc đã tự động chạy)
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Truy Vấn", color = appBlue, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // 3. Hiển thị danh sách đã gom nhóm
            GroupedOrderList(groupedOrders, navController)
        }
    }
}

// Hiển thị danh sách theo nhóm ngày (Giống bản Mock)
@Composable
fun GroupedOrderList(
    groupedOrders: Map<String, List<Order>>,
    navController: NavController
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        groupedOrders.forEach { (dateString, orders) ->
            // Tiêu đề ngày
            item {
                Text(
                    text = dateString,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
                )
            }
            // Thẻ chứa các đơn hàng trong ngày đó
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column {
                        orders.forEachIndexed { index, order ->
                            OrderItemRow(
                                order = order,
                                isLast = index == orders.lastIndex,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemRow(order: Order, isLast: Boolean, navController: NavController) {
    val dividerColor = Color(0xFFE0E0E0)
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // --- LOGIC ĐIỀU HƯỚNG QUAN TRỌNG ---
                // Gửi object 'order' sang màn hình Bill
                navController.currentBackStackEntry?.savedStateHandle?.set("order", order)
                navController.navigate(Routes.BILL)
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Hóa đơn
            Icon(Icons.Default.Description, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))

            Spacer(Modifier.width(12.dp))

            // Thông tin giữa (Mã đơn, Khách, Giờ)
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(order.id, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    // Hiển thị giờ (ví dụ: 14:30)
                    Text(timeFormat.format(Date(order.date)), color = Color.Gray, fontSize = 12.sp)
                }
                Text(order.customerName, color = Color.DarkGray, fontSize = 13.sp)
            }

            // Tổng tiền
            Text(
                text = "${"%,.0f".format(order.tongTien).replace(",", ".")}₫",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF0088FF), // Màu xanh cho tiền
                modifier = Modifier.padding(end = 8.dp)
            )

            // Mũi tên
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
        }

        // Đường kẻ (không hiện ở dòng cuối cùng trong nhóm)
        if (!isLast) {
            Divider(color = dividerColor, thickness = 0.5.dp, modifier = Modifier.padding(start = 52.dp, end = 16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputBox(
    label: String,
    date: String,
    modifier: Modifier,
    appBlue: Color,
    onDateSelected: (String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(6.dp))
            .clickable { showPicker = true }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(label, color = Color.Gray, fontSize = 12.sp)
                Text(date, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = appBlue, modifier = Modifier.size(20.dp))
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@TextButton
                    val formatted = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(millis))
                    onDateSelected(formatted)
                    showPicker = false
                }) { Text("Chọn", color = appBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Hủy", color = Color.Gray) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(state = datePickerState)
        }
    }
}