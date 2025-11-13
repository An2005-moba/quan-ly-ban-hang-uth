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
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.model.ProductItem
import com.nhom10.quanlybanhang.model.TransactionWithProducts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val appBlue = Color(0xFF3388FF)
    val grayBackground = Color(0xFFF0F2F5)

    // 🔹 Ngày mặc định
    var fromDate by remember { mutableStateOf("25/09/2025") }
    var toDate by remember { mutableStateOf("29/09/2025") }

    // 🔹 Dữ liệu giả
    val allTransactions = remember {
        mutableStateListOf(
            TransactionWithProducts(
                "DH:25013", "29/09/2025", "20:40", "Khách lẻ", 120000.0,
                listOf(ProductItem("SP01","Sản phẩm A",120000.0,100000.0,1,"Cái",false,""))
            ),
            TransactionWithProducts(
                "DH:25012", "28/09/2025", "19:00", "Khách lẻ", 150000.0,
                listOf(ProductItem("SP02","Sản phẩm B",150000.0,120000.0,1,"Cái",false,""))
            ),
            TransactionWithProducts(
                "DH:25011", "27/09/2025", "18:00", "Khách lẻ", 1000000.0,
                listOf(ProductItem("SP03","Sản phẩm C",1000000.0,800000.0,1,"Cái",true,""))
            ),
            TransactionWithProducts(
                "DH:25010", "25/09/2025", "15:00", "Khách lẻ", 1500000.0,
                listOf(ProductItem("SP04","Sản phẩm D",1500000.0,1200000.0,1,"Cái",true,""))
            ),
        )
    }

    // 🔹 Lưu trữ kết quả lọc
    var filteredTransactions by remember { mutableStateOf(allTransactions.groupBy { it.date }) }

    fun filterTransactions() {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val start = try { dateFormatter.parse(fromDate) } catch (e: Exception) { null }
        val end = try { dateFormatter.parse(toDate) } catch (e: Exception) { null }
        if (start == null || end == null) return

        filteredTransactions = allTransactions.filter {
            val tDate = dateFormatter.parse(it.date)
            tDate != null && !tDate.before(start) && !tDate.after(end)
        }.groupBy { it.date }
    }

    DisposableEffect(Unit) { filterTransactions(); onDispose { } }

    Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(grayBackground)
                .padding(paddingValues)
        ) {
            // --- 🔹 Filter Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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
                    Text("Truy vấn giao dịch", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DateInputBox("Từ ngày", fromDate, Modifier.weight(1f), appBlue) { fromDate = it }
                        DateInputBox("Đến ngày", toDate, Modifier.weight(1f), appBlue) { toDate = it }
                    }
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { filterTransactions() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Truy Vấn", color = appBlue, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            TransactionListWithDateGroup(filteredTransactions, navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputBox(label: String, date: String, modifier: Modifier, appBlue: Color, onDateSelected: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())

    Box(
        modifier = modifier
            .background(Color.White, RoundedCornerShape(4.dp))
            .clickable { showPicker = true }
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(date, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Icon(Icons.Default.CalendarToday, contentDescription = label, tint = Color.Gray, modifier = Modifier.size(20.dp))
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
                }) { Text("Chọn", color = appBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Hủy", color = appBlue) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun TransactionItemRow(item: TransactionWithProducts, isLast: Boolean, navController: NavController) {
    val dividerColor = Color(0xFFE0E0E0)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.currentBackStackEntry?.savedStateHandle?.set("transaction", item)
                navController.navigate(Routes.BILL)
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Description, contentDescription = "Hóa đơn", tint = Color.DarkGray, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(item.transactionId, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text(item.customerName, color = Color.DarkGray, fontSize = 13.sp)
                }
                Text(item.time, color = Color.Gray, fontSize = 12.sp)
            }
            Text("${"%,.0f".format(item.amount).replace(",", ".")}₫", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black, modifier = Modifier.padding(end = 8.dp))
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Chi tiết", tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
        if (!isLast) Divider(color = dividerColor, thickness = 1.dp, modifier = Modifier.padding(start = 52.dp, end = 16.dp))
    }
}

@Composable
fun TransactionListWithDateGroup(groupedTransactions: Map<String, List<TransactionWithProducts>>, navController: NavController) {
    LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        groupedTransactions.forEach { (date, items) ->
            item { Text(date, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 4.dp)) }
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                    Column { items.forEachIndexed { idx, t -> TransactionItemRow(t, idx == items.lastIndex, navController) } }
                }
            }
        }
    }
}
