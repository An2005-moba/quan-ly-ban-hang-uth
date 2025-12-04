package com.nhom10.quanlybanhang.ui.screens.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.text.DecimalFormat

// --- 1. Dialog Nhập Số (Cho Chiết khấu & Phụ phí) ---
@Composable
fun NumpadDialog(
    title: String,
    initialValue: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    // Chuyển giá trị double sang chuỗi để dễ nhập liệu (xóa .0 nếu là số nguyên)
    var displayValue by remember {
        mutableStateOf(if (initialValue == 0.0) "0" else DecimalFormat("#").format(initialValue))
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp)) // Căn giữa tiêu đề giả
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hiển thị số tiền
                Text(
                    text = displayValue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Bàn phím số
                val buttons = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("000", "0", "backspace")
                )

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    buttons.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                NumpadButton(
                                    symbol = key,
                                    modifier = Modifier.weight(1f).height(60.dp),
                                    onClick = {
                                        if (key == "backspace") {
                                            if (displayValue.isNotEmpty()) {
                                                displayValue = displayValue.dropLast(1)
                                                if (displayValue.isEmpty()) displayValue = "0"
                                            }
                                        } else {
                                            if (displayValue == "0") displayValue = key else displayValue += key
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút xác nhận
                TextButton(
                    onClick = {
                        onConfirm(displayValue.toDoubleOrNull() ?: 0.0)
                        onDismiss()
                    }
                ) {
                    Text("Xác nhận", fontSize = 18.sp, color = Color(0xFF0088FF), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun NumpadButton(symbol: String, modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)) // Màu xám nhạt cho nút
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (symbol == "backspace") {
            Icon(Icons.Default.Backspace, contentDescription = null, tint = Color.Black)
        } else {
            Text(text = symbol, fontSize = 24.sp, fontWeight = FontWeight.Medium, color = Color.Black)
        }
    }
}

// --- 2. Dialog Ghi Chú ---
@Composable
fun NoteDialog(
    initialNote: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var note by remember { mutableStateOf(initialNote) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Ghi chú", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    placeholder = { Text("Nhập ghi chú") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Hủy", color = Color.Gray)
                    }
                    TextButton(onClick = {
                        onConfirm(note)
                        onDismiss()
                    }) {
                        Text("Lưu", color = Color(0xFF0088FF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
@Composable
fun DiscountDialog(
    initialValue: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    // Hiển thị số nguyên nếu có thể (ví dụ 2.0 -> "2")
    var displayValue by remember {
        mutableStateOf(if (initialValue == 0.0) "0" else DecimalFormat("#").format(initialValue))
    }
    val appBlueColor = Color(0xFF0088FF)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(text = "Chiết khấu (%)", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Đóng")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hiển thị số + dấu %
                Text(
                    text = "$displayValue %",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = appBlueColor,
                    modifier = Modifier.padding(vertical = 24.dp)
                )

                // Bàn phím số
                val buttons = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("000", "0", "backspace")
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    buttons.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { key ->
                                NumpadButton(
                                    symbol = key,
                                    modifier = Modifier.weight(1f).height(60.dp),
                                    onClick = {
                                        if (key == "backspace") {
                                            if (displayValue.isNotEmpty()) {
                                                displayValue = displayValue.dropLast(1)
                                                if (displayValue.isEmpty()) displayValue = "0"
                                            }
                                        } else {
                                            // Logic nhập số: Nếu đang là 0 thì thay thế, ngược lại cộng thêm
                                            val newValueStr = if (displayValue == "0") key else displayValue + key
                                            val newValue = newValueStr.toDoubleOrNull() ?: 0.0

                                            // Chặn nhập quá 100%
                                            if (newValue <= 100) {
                                                displayValue = newValueStr
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Nút xác nhận
                TextButton(
                    onClick = {
                        onConfirm(displayValue.toDoubleOrNull() ?: 0.0)
                        onDismiss()
                    }
                ) {
                    Text("Xác nhận", fontSize = 18.sp, color = appBlueColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}