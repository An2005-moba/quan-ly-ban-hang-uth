package com.nhom10.quanlybanhang.ui.screens.cart

// --- THÊM CÁC IMPORT NÀY ---
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.nhom10.quanlybanhang.model.OrderItem
import com.nhom10.quanlybanhang.service.OrderViewModel
// -----------------------------

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
import java.text.DecimalFormat

/**
 * Màn hình Đơn hàng (Giỏ hàng)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    orderViewModel: OrderViewModel // THÊM: Nhận OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Màu nền xám nhạt
    val context = LocalContext.current // THÊM: Để dùng Toast

    // THÊM: Lấy dữ liệu từ ViewModel
    val cartItems by orderViewModel.cartItems.collectAsState()
    val selectedCustomer by orderViewModel.selectedCustomer.collectAsState()
    val totalAmount = orderViewModel.calculateTotal() // Tính tổng

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
                        Icon(Icons.Default.CalendarToday, "thêm item") // Icon này nên là Add
                    }
                    IconButton(onClick = { navController.navigate(Routes.SELECT_CUSTOMER) }) {
                        Icon(Icons.Default.Person, "Khách hàng")
                    }
                    IconButton(onClick = {
                        // SỬA: Xóa giỏ hàng
                        orderViewModel.clearCart()
                        Toast.makeText(context, "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show()
                    }) {
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
                    // === SỬA LOGIC Ở ĐÂY ===
                    onClick = {
                        // 1. Gọi ViewModel để lưu đơn hàng lên Firebase
                        orderViewModel.checkout(
                            onSuccess = {
                                // 2. Nếu lưu thành công, mới chuyển sang màn hình Thanh toán
                                Toast.makeText(context, "Tạo đơn hàng thành công!", Toast.LENGTH_SHORT).show()
                                navController.navigate(Routes.PAYMENT)
                            },
                            onFailure = { e ->
                                // 3. Nếu lỗi, thông báo
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    // ======================
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = cartItems.isNotEmpty() // THÊM: Chỉ bật khi có hàng
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
                    // SỬA: Truyền dữ liệu động
                    OrderInfoSection(
                        orderId = "Đơn hàng mới", // Tạm
                        customerName = selectedCustomer?.tenKhachHang ?: "Chưa chọn"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    // SỬA: Truyền dữ liệu động
                    ProductListSection(
                        navController = navController,
                        items = cartItems
                    )
                }

                // --- Khối 2: Tóm tắt thanh toán ---
                // SỬA: Truyền dữ liệu động
                SummarySection(
                    totalAmount = totalAmount
                )

                Spacer(modifier = Modifier.height(0.dp)) // Spacer đẩy bottom
            }
        }
    )
}

// --- CÁC COMPOSABLE PHỤ TRỢ (ĐÃ SỬA) ---

@Composable
private fun OrderInfoSection(
    orderId: String, // SỬA: Nhận tham số
    customerName: String // SỬA: Nhận tham số
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoRow(label = "Đơn hàng", value = orderId)
        InfoRow(label = "Khách hàng", value = customerName)
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
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProductListSection(
    navController: NavController,
    items: List<OrderItem> // SỬA: Nhận danh sách OrderItem
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SỬA: Kiểm tra giỏ hàng trống
        if (items.isEmpty()) {
            Text(
                text = "Chưa có sản phẩm nào",
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally)
            )
        } else {
            // SỬA: Dùng vòng lặp
            items.forEach { item ->
                val formatter = DecimalFormat("#,###")
                val giaBanFormatted = formatter.format(item.giaBan)
                val totalFormatted = formatter.format(item.giaBan * item.soLuong)

                ProductItem(
                    title = item.tenMatHang,
                    subtitle = "$giaBanFormatted x ${item.soLuong} ${item.donViTinh}",
                    total = totalFormatted,
                    onClick = {
                        // TODO: Chuyển item ID sang EditOrderItemScreen
                        // Tạm thời chỉ điều hướng
                        navController.navigate(Routes.EDIT_ORDER_ITEM)
                    }
                )
            }
        }
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
                fontWeight = FontWeight.Normal
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
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun SummarySection(
    totalAmount: Double // SỬA: Nhận tổng tiền
) {
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

        // SỬA: Hiển thị tổng tiền động
        val formatter = DecimalFormat("#,###")
        val totalFormatted = formatter.format(totalAmount)
        SummaryTotalRow(label = "Tổng tiền", total = totalFormatted)
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
            color = Color.Black,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
