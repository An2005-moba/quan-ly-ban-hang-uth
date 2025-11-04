package com.nhom10.quanlybanhang.ui.screens.report // Đảm bảo tên gói đúng

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Composable chính cho màn hình Báo cáo.
 * File này CHỈ chứa phần nội dung MÀU TRẮNG.
 * Thanh TopBar màu xanh đã được xử lý ở HomeScreen.kt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Thêm thanh cuộn
            .padding(16.dp) // Thêm padding chung
    ) {
        // --- NÚT LỌC ĐÃ BỊ XÓA KHỎI ĐÂY ---
        // Bắt đầu trực tiếp với Thẻ tóm tắt

        SummaryCards()

        Spacer(modifier = Modifier.height(24.dp))

        // --- PHẦN HÓA ĐƠN ---
        InvoiceSection()

        Spacer(modifier = Modifier.height(24.dp))

        // --- PHẦN THANH TOÁN ---
        PaymentSection()
    }
}

/**
 * Hai thẻ màu "Lợi nhuận" và "Doanh thu"
 */
@Composable
private fun SummaryCards() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa 2 thẻ
    ) {
        // Thẻ Lợi nhuận (Màu cam)
        SummaryCard(
            title = "Lợi nhuận",
            amount = "0", // Theo yêu cầu
            color = Color(0xFFFAA653), // Mã màu cam
            modifier = Modifier.weight(1f)
        )
        // Thẻ Doanh thu (Màu xanh)
        SummaryCard(
            title = "Doanh thu",
            amount = "0", // Theo yêu cầu
            color = Color(0xFF5DBF89), // Mã màu xanh
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Composable phụ trợ để vẽ một thẻ tóm tắt
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp), // Đặt chiều cao
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = amount,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Phần hiển thị chi tiết "Hóa đơn"
 */
@Composable
private fun InvoiceSection() {
    Text(
        text = "Hóa đơn",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    // Hàng trên: Số hóa đơn, Giá trị, Tiền thuế
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ReportStatItem(value = "0", label = "Số hóa đơn", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Giá trị hóa đơn", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Tiền thuế", modifier = Modifier.weight(1f))
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Hàng dưới: Giảm giá, Tiền bán, Tiền vốn
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ReportStatItem(value = "0", label = "Giảm giá", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Tiền bán", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Tiền vốn", modifier = Modifier.weight(1f))
    }
}

/**
 * Phần hiển thị chi tiết "Thanh toán"
 */
@Composable
private fun PaymentSection() {
    // Thanh ngang chia cách
    Divider(modifier = Modifier.padding(vertical = 16.dp))

    Text(
        text = "Thanh toán",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 16.dp)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ReportStatItem(value = "0", label = "Tiền mặt", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Ngân hàng", modifier = Modifier.weight(1f))
        ReportStatItem(value = "0", label = "Khách nợ", modifier = Modifier.weight(1f))
    }
}

/**
 * Composable phụ trợ để hiển thị một chỉ số (gồm số và nhãn)
 */
@Composable
private fun ReportStatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start // Căn trái như trong ảnh
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}

/**
 * Hàm xem trước (Preview)
 */
@Preview(showBackground = true)
@Composable
fun ReportScreenPreview() {
    // Bọc trong một Theme để xem trước
    // QuanLyBanHangTheme {
    ReportScreen()
    // }
}