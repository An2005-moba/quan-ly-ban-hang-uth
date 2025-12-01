package com.nhom10.quanlybanhang.ui.screens.report

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.viewmodel.ReportViewModel
import com.nhom10.quanlybanhang.viewmodel.ReportUiState
import com.nhom10.quanlybanhang.viewmodel.StatusFilter
import com.nhom10.quanlybanhang.viewmodel.TimeFilter
import java.text.DecimalFormat
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    viewModel: ReportViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeFilter by viewModel.timeFilter.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    // Auto load data khi mở màn hình
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // --- 1. FILTER BAR (BỘ LỌC) ---
        FilterSection(
            currentTimeFilter = timeFilter,
            currentStatusFilter = statusFilter,
            onTimeSelected = { viewModel.setTimeFilter(it) },
            onStatusSelected = { viewModel.setStatusFilter(it) },
            onCustomDateSelected = { start, end -> viewModel.setCustomDateRange(start, end) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. THẺ TÓM TẮT ---
        SummaryCards(uiState)

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. PHẦN HÓA ĐƠN ---
        InvoiceSection(uiState)

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. PHẦN THANH TOÁN ---
        PaymentSection(uiState)
    }
}

@Composable
fun FilterSection(
    currentTimeFilter: TimeFilter,
    currentStatusFilter: StatusFilter,
    onTimeSelected: (TimeFilter) -> Unit,
    onStatusSelected: (StatusFilter) -> Unit,
    onCustomDateSelected: (Long, Long) -> Unit
) {
    var timeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Hàm hiển thị DatePicker
    fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        // Chọn ngày bắt đầu
        DatePickerDialog(context, { _, startYear, startMonth, startDay ->
            val startCal = Calendar.getInstance()
            startCal.set(startYear, startMonth, startDay, 0, 0, 0)

            // Sau khi chọn ngày bắt đầu, hiện tiếp chọn ngày kết thúc
            DatePickerDialog(context, { _, endYear, endMonth, endDay ->
                val endCal = Calendar.getInstance()
                endCal.set(endYear, endMonth, endDay, 23, 59, 59)

                onCustomDateSelected(startCal.timeInMillis, endCal.timeInMillis)
                onTimeSelected(TimeFilter.CUSTOM) // Kích hoạt filter Custom
            }, startYear, startMonth, startDay).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Row(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        // --- BÊN TRÁI: THỜI GIAN ---
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = { timeExpanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF007AFF)),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = when(currentTimeFilter) {
                        TimeFilter.TODAY -> "Hôm nay"
                        TimeFilter.THIS_WEEK -> "Tuần này"
                        TimeFilter.THIS_MONTH -> "Tháng này"
                        TimeFilter.CUSTOM -> "Tùy chỉnh"
                    },
                    color = Color(0xFF007AFF)
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF007AFF))
            }

            DropdownMenu(expanded = timeExpanded, onDismissRequest = { timeExpanded = false }) {
                DropdownMenuItem(text = { Text("Hôm nay") }, onClick = { onTimeSelected(TimeFilter.TODAY); timeExpanded = false })
                DropdownMenuItem(text = { Text("Tuần này") }, onClick = { onTimeSelected(TimeFilter.THIS_WEEK); timeExpanded = false })
                DropdownMenuItem(text = { Text("Tháng này") }, onClick = { onTimeSelected(TimeFilter.THIS_MONTH); timeExpanded = false })
                DropdownMenuItem(text = { Text("Khác (Chọn ngày)") }, onClick = {
                    timeExpanded = false
                    showDateRangePicker()
                })
            }
        }

        Spacer(Modifier.width(16.dp))

        // --- BÊN PHẢI: TRẠNG THÁI ---
        Box(modifier = Modifier.weight(1f)) {
            Button(
                onClick = { statusExpanded = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFF007AFF)),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (currentStatusFilter == StatusFilter.DELETED) "Đã xóa" else "Tất cả",
                    color = Color(0xFF007AFF)
                )
                Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF007AFF))
            }

            DropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                DropdownMenuItem(text = { Text("Tất cả (Đơn hàng)") }, onClick = { onStatusSelected(StatusFilter.ALL); statusExpanded = false })
                DropdownMenuItem(text = { Text("Hóa đơn đã xóa") }, onClick = { onStatusSelected(StatusFilter.DELETED); statusExpanded = false })
            }
        }
    }
}

val formatter = DecimalFormat("#,###")

@Composable
private fun SummaryCards(state: ReportUiState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SummaryCard(
            title = "Lợi nhuận",
            amount = formatter.format(state.loiNhuan),
            color = Color(0xFFFAA653),
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            title = "Doanh thu",
            amount = formatter.format(state.doanhThu),
            color = Color(0xFF5DBF89),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InvoiceSection(state: ReportUiState) {
    Text(
        text = "Hóa đơn",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ReportStatItem(value = state.soHoaDon.toString(), label = "Số hóa đơn", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.giaTriHoaDon), label = "Giá trị hóa đơn", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.tienThue), label = "Tiền thuế", modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(16.dp))

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ReportStatItem(value = formatter.format(state.giamGia), label = "Giảm giá", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.tienBan), label = "Tiền bán", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.tienVon), label = "Tiền vốn", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun PaymentSection(state: ReportUiState) {
    Divider(modifier = Modifier.padding(vertical = 16.dp))

    Text(
        text = "Thanh toán",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        ReportStatItem(value = formatter.format(state.tienMat), label = "Tiền mặt", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.nganHang), label = "Ngân hàng", modifier = Modifier.weight(1f))
        ReportStatItem(value = formatter.format(state.khachNo), label = "Khách nợ", modifier = Modifier.weight(1f))
    }
}

// Các hàm phụ trợ SummaryCard, ReportStatItem giữ nguyên như file cũ của bạn
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummaryCard(title: String, amount: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = amount, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = title, style = MaterialTheme.typography.bodyMedium, color = Color.White)
        }
    }
}

@Composable
private fun ReportStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.Start) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}