package com.nhom10.quanlybanhang.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.data.model.OrderItem
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    orderViewModel: OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    val context = LocalContext.current

    // Lấy dữ liệu từ ViewModel
    val cartItems by orderViewModel.cartItems.collectAsState()
    val selectedCustomer by orderViewModel.selectedCustomer.collectAsState()
    val totalAmount by orderViewModel.totalAmount.collectAsState()

    // Lưu ý: ViewModel dùng discountPercent
    val discountPercent by orderViewModel.discountPercent.collectAsState()
    val surcharge by orderViewModel.surcharge.collectAsState()
    val note by orderViewModel.note.collectAsState()
    val isTaxEnabled by orderViewModel.isTaxEnabled.collectAsState()

    // State dialog
    var showDiscountDialog by remember { mutableStateOf(false) }
    var showSurchargeDialog by remember { mutableStateOf(false) }
    var showNoteDialog by remember { mutableStateOf(false) }
    val currentOrderId by orderViewModel.currentOrderId.collectAsState()

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đơn hàng", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.ADD_ORDER_ITEM) }) {
                        Icon(Icons.Default.CalendarToday, "thêm item")
                    }
                    IconButton(onClick = { navController.navigate(Routes.SELECT_CUSTOMER) }) {
                        Icon(Icons.Default.Person, "Khách hàng")
                    }
                    IconButton(onClick = {
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
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {
                Button(
                    onClick = {
                        // KHÔNG gọi orderViewModel.checkout() ở đây nữa
                        // Chỉ chuyển màn hình
                        navController.navigate(Routes.PAYMENT)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp),
                    enabled = cartItems.isNotEmpty()
                ) {
                    Text("Thanh toán", modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(0.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                ) {
                    OrderInfoSection(
                        orderId = currentOrderId,
                        customerName = selectedCustomer?.tenKhachHang ?: "Chưa chọn"
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                    ProductListSection(navController, cartItems)
                }

                // Gọi SummarySection với tham số discountPercent
                SummarySection(
                    totalAmount = totalAmount,
                    discountPercent = discountPercent, // SỬA: Truyền %
                    surcharge = surcharge,
                    note = note,
                    isTaxEnabled = isTaxEnabled,
                    onEditDiscount = { showDiscountDialog = true },
                    onEditSurcharge = { showSurchargeDialog = true },
                    onEditNote = { showNoteDialog = true },
                    onToggleTax = { orderViewModel.toggleTax(it) }
                )
                Spacer(modifier = Modifier.height(0.dp))
            }

            // Hiển thị Dialog
            if (showDiscountDialog) {
                DiscountDialog( // Dùng DiscountDialog mới (chỉ nhập số)
                    initialValue = discountPercent,
                    onDismiss = { showDiscountDialog = false },
                    onConfirm = { orderViewModel.updateDiscount(it) }
                )
            }
            if (showSurchargeDialog) {
                NumpadDialog(
                    title = "Phụ phí",
                    initialValue = surcharge,
                    onDismiss = { showSurchargeDialog = false },
                    onConfirm = { orderViewModel.updateSurcharge(it) }
                )
            }
            if (showNoteDialog) {
                NoteDialog(
                    initialNote = note,
                    onDismiss = { showNoteDialog = false },
                    onConfirm = { orderViewModel.updateNote(it) }
                )
            }
        }
    )
}

// --- CÁC HÀM PHỤ TRỢ ---

@Composable
private fun OrderInfoSection(orderId: String, customerName: String) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoRow(label = "Đơn hàng", value = orderId)
        InfoRow(label = "Khách hàng", value = customerName)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
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
private fun ProductListSection(navController: NavController, items: List<OrderItem>) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        if (items.isEmpty()) {
            Text(
                text = "Chưa có sản phẩm nào",
                color = Color.Gray,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            items.forEach { item ->
                val formatter = DecimalFormat("#,###")
                Product(
                    title = item.tenMatHang,
                    subtitle = "${formatter.format(item.giaBan)} x ${item.soLuong} ${item.donViTinh}",
                    total = formatter.format(item.giaBan * item.soLuong),
                    onClick = { navController.navigate(Routes.EDIT_ORDER_ITEM) }
                )
            }
        }
    }
}

@Composable
private fun Product(title: String, subtitle: String, total: String, onClick: () -> Unit) {
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
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
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
    discountPercent: Double, // SỬA: Nhận %
    surcharge: Double,
    note: String,
    isTaxEnabled: Boolean,
    totalAmount: Double,
    onEditDiscount: () -> Unit,
    onEditSurcharge: () -> Unit,
    onEditNote: () -> Unit,
    onToggleTax: (Boolean) -> Unit
) {
    val formatter = DecimalFormat("#,###")
    val percentFormatter = DecimalFormat("#.##") // Định dạng số lẻ cho %

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Chiết khấu (Hiển thị %)
        SummaryRowAction(
            label = "Chiết khấu",
            value = "${percentFormatter.format(discountPercent)} %", // Thêm dấu %
            icon = Icons.Default.Edit,
            isTaxRow = false,
            onClick = onEditDiscount
        )

        // 2. Phụ phí
        SummaryRowAction(
            label = "Phụ phí",
            value = formatter.format(surcharge),
            icon = Icons.Default.Edit,
            isTaxRow = false,
            onClick = onEditSurcharge
        )

        // 3. Ghi chú
        SummaryRowAction(
            label = "Ghi chú",
            value = null,
            icon = Icons.Default.Edit,
            isTaxRow = false,
            isChecked = note.isNotEmpty(),
            onClick = onEditNote
        )

        // 4. Thuế
        SummaryRowAction(
            label = "Thuế",
            value = "0",
            icon = Icons.Default.CheckBox,
            isTaxRow = true,
            isChecked = isTaxEnabled,
            onCheckedChange = onToggleTax
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        SummaryTotalRow(label = "Tổng tiền", total = formatter.format(totalAmount))
    }
}

@Composable
private fun SummaryTotalRow(label: String, total: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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

@Composable
private fun SummaryRowAction(
    label: String,
    value: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isTaxRow: Boolean,
    isChecked: Boolean = true,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val appBlueColor = Color(0xFF0088FF)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isTaxRow) { onClick?.invoke() },
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

        Box(
            modifier = Modifier.size(24.dp), // Kích thước cố định
            contentAlignment = Alignment.Center
        ) {
            if (isTaxRow) {
                // Checkbox cho Thuế
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = appBlueColor),
                    // Tùy chọn: Thêm modifier scale nếu muốn ô vuông nhỏ lại cho cân đối
                    // modifier = Modifier.scale(0.8f)
                )
            } else {
                // Icon Edit cho các mục khác
                IconButton(
                    onClick = { onClick?.invoke() },
                    modifier = Modifier.size(24.dp) // Đảm bảo Icon cũng 24dp
                ) {
                    if (label == "Ghi chú") {
                        // Icon checkbox hiển thị trạng thái ghi chú
                        Icon(
                            imageVector = if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                            contentDescription = null,
                            tint = if (isChecked) appBlueColor else Color.Gray
                        )
                    } else {
                        // Icon cây viết nền xanh
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(appBlueColor, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                }
            }
        }
    }