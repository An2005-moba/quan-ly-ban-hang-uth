package com.nhom10.quanlybanhang.ui.screens.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.viewmodel.ReportViewModel
import com.nhom10.quanlybanhang.viewmodel.ReportUiState
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    // CHANGED: Nhận viewModel từ bên ngoài thay vì tự khởi tạo
    viewModel: ReportViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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

        SummaryCards(uiState)

        Spacer(modifier = Modifier.height(24.dp))


        InvoiceSection(uiState)

        Spacer(modifier = Modifier.height(24.dp))


        PaymentSection(uiState)
    }
}

// --- CHANGED: Đã xóa FilterSection Composable ---

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