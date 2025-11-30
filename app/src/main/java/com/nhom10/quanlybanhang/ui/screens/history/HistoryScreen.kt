package com.nhom10.quanlybanhang.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.data.model.Order
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    orderViewModel: com.nhom10.quanlybanhang.viewmodel.OrderViewModel
) {
    val appBlue = Color(0xFF3388FF)

    var fromDate by remember { mutableStateOf("01/01/2000") }
    var toDate by remember { mutableStateOf("31/12/2100") }

    val orderHistory by orderViewModel.orderHistory.collectAsState()

    val filteredOrders by remember(fromDate, toDate, orderHistory) {
        derivedStateOf {
            val df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val start = runCatching { df.parse(fromDate)?.time ?: 0L }.getOrDefault(0L)
            val end = runCatching {
                (df.parse(toDate)?.time ?: 0L) + 86399999
            }.getOrDefault(Long.MAX_VALUE)

            orderHistory.filter { order -> order.date in start..end }
                .sortedByDescending { it.date }
        }
    }

    val groupedOrders by remember(filteredOrders) {
        derivedStateOf {
            filteredOrders.groupBy { order ->
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(order.date))
            }
        }
    }

    LaunchedEffect(Unit) { orderViewModel.loadOrderHistory() }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) // DARK MODE
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = appBlue),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Truy vấn giao dịch",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DateInputBox("Từ ngày", fromDate, Modifier.weight(1f)) { fromDate = it }
                        DateInputBox("Đến ngày", toDate, Modifier.weight(1f)) { toDate = it }
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Truy Vấn",
                            color = appBlue,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            GroupedOrderList(groupedOrders, navController)
        }
    }
}

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

            item {
                Text(
                    text = dateString,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // DARK MODE
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface // DARK MODE
                    ),
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
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(order.date))

    // LOGIC RÚT GỌN ID GIAO DỊCH (10 ký tự)
    val MAX_ID_LENGTH = 10
    val displayOrderId = if (order.id.length > MAX_ID_LENGTH) {
        order.id.substring(0, MAX_ID_LENGTH) + "..."
    } else {
        order.id
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
            // 1. ICON
            Icon(
                Icons.Default.Description,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )

            // 2. SPACER
            Spacer(Modifier.width(12.dp))

            // 3. THÔNG TIN GIAO DỊCH TRUNG TÂM (ID, Tên khách hàng)
            Column(modifier = Modifier.weight(1f)) {

                // Hàng 1: ID GIAO DỊCH (Đã rút gọn)
                Text(
                    displayOrderId,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Hàng 2: TÊN KHÁCH HÀNG
                Text(
                    order.customerName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } // END Column

            // 4. KHỐI THÔNG TIN BÊN PHẢI (Thời gian VÀ Số tiền)
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 12.dp)
            ) {
                // Hàng 1: THỜI GIAN (16:18)
                Text(
                    formattedTime,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.wrapContentWidth()
                )

                // Hàng 2: SỐ TIỀN GIAO DỊCH (200.000đ)
                Text(
                    text = "${"%,.0f".format(order.tongTien).replace(",", ".")}₫",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF0088FF),
                    modifier = Modifier.wrapContentWidth()
                )
            }

            // 5. ICON MŨI TÊN (Giữ nguyên vị trí ở ngoài cùng)
            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(20.dp)
                    .padding(start = 4.dp)
            )
        }

        if (!isLast) {
            Divider(
                color = MaterialTheme.colorScheme.outlineVariant, // DARK MODE
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 52.dp, end = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputBox(
    label: String,
    date: String,
    modifier: Modifier,
    onDateSelected: (String) -> Unit
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Box(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surface, // DARK MODE
                RoundedCornerShape(6.dp)
            )
            .clickable { showPicker = true }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(
                    label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
                Text(
                    date,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
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
                }) { Text("Chọn") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Hủy") }
            },
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surface // DARK MODE
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }
}