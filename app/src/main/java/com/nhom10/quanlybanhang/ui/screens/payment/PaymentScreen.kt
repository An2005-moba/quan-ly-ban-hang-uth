package com.nhom10.quanlybanhang.ui.screens.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack // <-- THÊM IMPORT NÀY
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)

    var khachTraInput by remember { mutableStateOf("0") }

    // === LOGIC TÍNH TOÁN ===
    val tongHoaDon = 300000.0
    val khachTraDouble = khachTraInput.toDoubleOrNull() ?: 0.0
    val chenhLech = khachTraDouble - tongHoaDon

    val (nhanHienThi, soTienHienThi) = if (chenhLech >= 0) {
        "Tiền thừa:" to chenhLech
    } else {
        "Khách thiếu:" to -chenhLech
    }

    val formatter = DecimalFormat("#,###")
    val khachTraFormatted = formatter.format(khachTraDouble.toLong())
    val soTienHienThiFormatted = formatter.format(soTienHienThi.toLong())
    // =================================

    Scaffold(
        containerColor = Color.White,

        // === SỬA TẠI ĐÂY: THÊM LẠI TOPBAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thành tiền", fontWeight = FontWeight.Bold)
                },
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
        // ===================================

        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        navController.navigate(
                            Routes.invoiceRoute(
                                khachTra = khachTraFormatted,
                                tienThua = soTienHienThiFormatted
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = chenhLech >= 0
                ) {
                    Text(
                        text = "Thanh toán",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // ... (TabRow)
                TabRow(
                    selectedTabIndex = 0, // "Tiền mặt" (index 0) luôn được chọn
                    containerColor = Color.White,
                    contentColor = appBlueColor,
                    indicator = {}
                ) {
                    // Tab "Tiền Mặt"
                    PaymentTab(
                        text = "Tiền Mặt",
                        isSelected = true, // Luôn được chọn
                        onClick = { /* Không làm gì */ }
                    )
                    // Tab "Ngân hàng" (hoạt động như nút điều hướng)
                    PaymentTab(
                        text = "Ngân hàng",
                        isSelected = false, // Không bao giờ được chọn
                        onClick = { navController.navigate(Routes.BANK_PAYMENT) } // Chuyển màn hình
                    )
                }
                // ... (Phần hiển thị tiền)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("Khách trả:")
                    Text(
                        text = khachTraFormatted,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("$nhanHienThi $soTienHienThiFormatted")
                }
                // ... (CalculatorPad)
                CalculatorPad(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(scaffoldBgColor),
                    onNumberClick = { num ->
                        if (khachTraInput == "0") khachTraInput = num else khachTraInput += num
                    },
                    onBackspace = {
                        khachTraInput =
                            if (khachTraInput.length > 1) khachTraInput.dropLast(1) else "0"
                    },
                    onClear = { khachTraInput = "0" }
                )
            }
        }
    )
}

// ... (Tất cả Composable phụ trợ PaymentTab, CalculatorPad, CalculatorButton giữ nguyên) ...
@Composable
private fun PaymentTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color.White else Color(0xFFF0F2F5)
    val textColor = if (isSelected) Color.Black else Color.Gray

    Tab(
        selected = isSelected,
        onClick = onClick,
        modifier = Modifier.background(bgColor)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun CalculatorPad(
    modifier: Modifier = Modifier,
    onNumberClick: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit
) {
    val buttonModifier = Modifier
        .padding(4.dp)
        .aspectRatio(1.5f)
        .clip(RoundedCornerShape(8.dp))
        .background(Color.White)

    Column(
        modifier = modifier.padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "7",
                onClick = { onNumberClick("7") })
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "8",
                onClick = { onNumberClick("8") })
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "9",
                onClick = { onNumberClick("9") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "4",
                onClick = { onNumberClick("4") })
            CalculatorButton(
                modifier = buttonModifier.weight(1F),
                text = "5",
                onClick = { onNumberClick("5") })
            CalculatorButton(
                modifier = buttonModifier.weight(1F),
                text = "6",
                onClick = { onNumberClick("6") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "1",
                onClick = { onNumberClick("1") })
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "2",
                onClick = { onNumberClick("2") })
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "3",
                onClick = { onNumberClick("3") })
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(modifier = buttonModifier.weight(1f), text = "C", onClick = onClear)
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                text = "0",
                onClick = { onNumberClick("0") })
            CalculatorButton(
                modifier = buttonModifier.weight(1f),
                icon = Icons.Default.Backspace,
                onClick = onBackspace
            )
        }
    }
}

@Composable
private fun CalculatorButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier.clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(text, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
        } else if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(28.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    PaymentScreen(navController = rememberNavController())
}