package com.nhom10.quanlybanhang.ui.screens.customer

import com.nhom10.quanlybanhang.data.model.Customer
import com.nhom10.quanlybanhang.viewmodel.CustomerViewModel
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import java.text.Normalizer
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCustomerScreen(
    navController: NavController,
    customerViewModel: CustomerViewModel, // THÊM: Nhận CustomerViewModel
    orderViewModel: OrderViewModel      // THÊM: Nhận OrderViewModel (để lưu lựa chọn)
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Màu nền xám nhạt
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // SỬA: Lấy dữ liệu từ ViewModel
    val customersFromDb by customerViewModel.customers.collectAsState()
    // THÊM: Tạo danh sách khách hàng đầy đủ (bao gồm "Khách lẻ")
    val customers = remember(customersFromDb) {
        listOf(
            Customer(id = "khach_le", tenKhachHang = "Khách lẻ", soDienThoai = "") // ID đặc biệt
        ) + customersFromDb
    }

    // SỬA: Lấy khách hàng đang được chọn từ OrderViewModel
    val selectedCustomer by orderViewModel.selectedCustomer.collectAsState()
    val selectedCustomerId = selectedCustomer?.id ?: "khach_le" // Mặc định là "Khách lẻ"\
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var customerToDelete by remember { mutableStateOf<Customer?>(null) }

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chọn khách hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    // NÚT 1: Xóa tất cả (Chỉ hiện khi có khách trong DB)
                    if (customersFromDb.isNotEmpty()) {
                        IconButton(onClick = { showDeleteAllDialog = true }) {
                            Icon(Icons.Default.DeleteForever, "Xóa tất cả", tint = Color.Red)
                        }
                    }
                    IconButton(onClick = { navController.navigate(Routes.ADD_CUSTOMER) }) {
                        Icon(Icons.Default.Add, "Thêm khách hàng")
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

        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // --- Thanh tìm kiếm ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Nhập tên, số điện thoại") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = lightGrayBorder,
                        focusedBorderColor = appBlueColor
                    )
                )

                // --- Danh sách khách hàng ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    // --- ĐOẠN CODE LOGIC TÌM KIẾM MỚI ---
                    val filteredList = customers.filter { customer ->
                        // 1. Chuẩn hóa từ khóa tìm kiếm
                        val cleanQuery = normalizeString(searchQuery)

                        // 2. Chuẩn hóa tên khách hàng trong data
                        val cleanName = normalizeString(customer.tenKhachHang)

                        // 3. Chuẩn hóa số điện thoại (chỉ giữ lại số)
                        val cleanPhone = customer.soDienThoai.replace(
                            "\\D".toRegex(),
                            ""
                        ) // Xóa mọi thứ không phải số
                        val queryPhone = searchQuery.replace(
                            "\\D".toRegex(),
                            ""
                        ) // Xóa mọi thứ không phải số trong từ khóa

                        // 4. So sánh
                        // - Tên: So sánh chuỗi đã chuẩn hóa
                        // - SĐT: Nếu người dùng nhập số, so sánh số
                        val isNameMatch = cleanName.contains(cleanQuery)
                        val isPhoneMatch =
                            if (queryPhone.isNotEmpty()) cleanPhone.contains(queryPhone) else false

                        isNameMatch || isPhoneMatch
                    }

                    items(filteredList) { customer ->
                        CustomerListItem(
                            customer = customer,
                            isSelected = customer.id == selectedCustomerId,
                            onClick = {
                                // SỬA: Cập nhật ViewModel và quay lại
                                orderViewModel.selectCustomer(customer)
                                navController.popBackStack()
                            },
                            // Gọi dialog khi nhấn nút xóa ở từng dòng
                            onDelete = { customerToDelete = customer }
                        )
                    }
                }
            }
        }
    )
    // --- HỘP THOẠI XÁC NHẬN XÓA TỪNG NGƯỜI ---
    if (customerToDelete != null) {
        AlertDialog(
            onDismissRequest = { customerToDelete = null },
            title = { Text("Xóa khách hàng") },
            text = { Text("Bạn có chắc muốn xóa '${customerToDelete?.tenKhachHang}' không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        customerToDelete?.let {
                            customerViewModel.deleteCustomer(it.id)
                        }
                        customerToDelete = null
                    }
                ) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { customerToDelete = null }) { Text("Hủy") }
            },
            containerColor = Color.White
        )
    }

    // --- HỘP THOẠI XÁC NHẬN XÓA TẤT CẢ ---
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text("Xóa tất cả") },
            text = { Text("Bạn có chắc muốn xóa TOÀN BỘ danh sách khách hàng không? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        customerViewModel.deleteAllCustomers()
                        showDeleteAllDialog = false
                    }
                ) { Text("Xóa hết", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAllDialog = false }) { Text("Hủy") }
            },
            containerColor = Color.White
        )
    }
}

@Composable
private fun CustomerListItem(
    customer: Customer, // SỬA: Đã dùng model thật
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit // Callback xóa
) {
    val appBlueColor = Color(0xFF0088FF)
    val imageBitmap = remember(customer.avatarUrl) {
        base64ToImageBitmap(customer.avatarUrl)
    }

    ListItem(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        headlineContent = { Text(customer.tenKhachHang) }, // SỬA: Dùng tenKhachHang
        supportingContent = {
            if (customer.soDienThoai.isNotEmpty()) { // SỬA: Dùng soDienThoai
                Text(customer.soDienThoai)
            }
        },
        leadingContent = {
            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = customer.tenKhachHang,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Nếu không có ảnh, hiện icon mặc định
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, "Đã chọn", tint = appBlueColor)
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // CHỈ HIỆN NÚT XÓA NẾU KHÔNG PHẢI "KHÁCH LẺ"
                if (customer.id != "khach_le") {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, "Xóa", tint = Color.Gray)
                    }
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    Divider() // Thêm đường kẻ
}

private fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    if (base64String.isEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}

// Hàm tiện ích: Chuyển chuỗi về dạng không dấu, viết thường, xóa khoảng trắng
fun normalizeString(input: String): String {
    val temp = Normalizer.normalize(input, Normalizer.Form.NFD)
    val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
    val result = pattern.matcher(temp).replaceAll("")
        .lowercase() // Chuyển về chữ thường
        .replace("đ", "d") // Xử lý chữ đ
        .replace("Đ", "d") // Xử lý chữ Đ
        .replace("\\s+".toRegex(), "") // Xóa toàn bộ khoảng trắng
    return result
}