package com.nhom10.quanlybanhang.ui.screens.cart


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes

/**
 * Màn hình Đơn hàng (Giỏ hàng)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Màu nền xám nhạt

    Scaffold(
        containerColor = scaffoldBgColor,
        // === 1. TOP BAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Đơn hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // Nhấn để quay lại
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.ADD_ORDER_ITEM) }) {
                        Icon(Icons.Default.CalendarToday, "thêm item")
                    }
                    IconButton(onClick = { navController.navigate(Routes.SELECT_CUSTOMER) }) {
                        Icon(Icons.Default.Person, "Khách hàng")
                    }
                    IconButton(onClick = { /* TODO: Xử lý xóa */ }) {
                        Icon(Icons.Default.Delete, "Xóa")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        // === 2. BOTTOM BAR (NÚT THANH TOÁN) ===
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Button(
                    // === THAY ĐỔI Ở ĐÂY ===
                    onClick = { navController.navigate(Routes.PAYMENT) }, // Sửa từ /* TODO */
                    // ======================
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Thanh toán",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        },

        // === 3. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()), // Cho phép cuộn
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(0.dp)) // Spacer đẩy top

                // --- Khối 1: Thông tin đơn hàng & Sản phẩm ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    OrderInfoSection()
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProductListSection(navController = navController)
                }

                // --- Khối 2: Tóm tắt thanh toán ---
                SummarySection()

                Spacer(modifier = Modifier.height(0.dp)) // Spacer đẩy bottom
            }
        }
    )
}

// --- CÁC COMPOSABLE PHỤ TRỢ ---

@Composable
private fun OrderInfoSection() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Đơn hàng", value = "DH.3636")
        InfoRow(label = "Khách hàng", value = "Tú")
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black // "Đơn hàng", "Khách hàng"
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black, // "DH.3636", "Tú"
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProductListSection(navController: NavController) { // <-- Thêm NavController
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProductItem(
            title = "Tôm",
            subtitle = "100.000 x 1kg",
            total = "100.000",
            onClick = { navController.navigate(Routes.EDIT_ORDER_ITEM) } // <-- Thêm onClick
        )
        ProductItem(
            title = "Cá",
            subtitle = "50.000 x 1kg",
            total = "50.000",
            onClick = { navController.navigate(Routes.EDIT_ORDER_ITEM) } // <-- Thêm onClick
        )
        ProductItem(
            title = "Cua",
            subtitle = "150.000 x 1kg",
            total = "150.000",
            onClick = { navController.navigate(Routes.EDIT_ORDER_ITEM) } // <-- Thêm onClick
        )
    }
}

@Composable
private fun ProductItem(title: String, subtitle: String, total: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.Top

    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal // Chữ "Tôm", "Cá", "Cua" không bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
        Text(
            text = total,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal // Giá tiền cũng không bold
        )
    }
}

@Composable
private fun SummarySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryRow(label = "Chiết khấu", value = "0", checked = true)
        SummaryRow(label = "Phụ phí", value = "0", checked = true)
        SummaryRow(label = "Ghi chú", value = null, checked = true) // Không có giá trị
        SummaryRow(label = "Thuế", value = "0", checked = true)
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SummaryTotalRow(label = "Tổng tiền", total = "300.000")
    }
}

@Composable
private fun SummaryRow(label: String, value: String?, checked: Boolean) {
    val appBlueColor = Color(0xFF0088FF)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
        Spacer(Modifier.weight(1f))
        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        // Icon Checkbox
        Icon(
            imageVector = Icons.Filled.CheckBox, // Luôn được check
            contentDescription = null,
            tint = appBlueColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun SummaryTotalRow(label: String, total: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = total,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black, // Tổng tiền màu đen
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    CartScreen(navController = rememberNavController())
}