package com.nhom10.quanlybanhang.ui.screens.cart

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditOrderItemScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    productViewModel: ProductViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    val context = LocalContext.current

    // Lấy item đang chọn (Sử dụng .value thay vì by để an toàn)
    val selectedItemState = orderViewModel.selectedOrderItem.collectAsState()
    val selectedItem = selectedItemState.value

    // Lấy danh sách sản phẩm để tìm giá gốc
    val productsState = productViewModel.products.collectAsState()
    val productList = productsState.value

    if (selectedItem == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        Box(Modifier.fillMaxSize().background(scaffoldBgColor))
        return
    }

    // --- LOGIC TÌM GIÁ GỐC VÀ TỒN KHO ---
    val currentProduct = remember(selectedItem, productList) {
        productList.find { it.documentId == selectedItem.productId }
    }
    val maxStock = (currentProduct?.soLuong ?: 0.0)
    val originalPrice = (currentProduct?.giaBan ?: selectedItem.giaBan) // Giá gốc từ danh sách sản phẩm

    // --- STATE ---
    var soLuong by remember { mutableStateOf(selectedItem.soLuong) }

    val formatter = DecimalFormat("#") // Định dạng số nguyên cho đẹp

    // Giá bán (Giá gốc)
    var giaBanStr by remember { mutableStateOf(formatter.format(selectedItem.giaBan)) }

    // Chiết khấu % (Lấy từ item, mặc định 0)
    var discountStr by remember { mutableStateOf(formatter.format(selectedItem.chietKhau)) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    // [INIT] Tính toán % chiết khấu ban đầu khi mở màn hình
    LaunchedEffect(Unit) {
        if (originalPrice > 0) {
            val currentPrice = selectedItem.giaBan
            // Công thức: % Giảm = (1 - Giá hiện tại / Giá gốc) * 100
            val initialDiscount = (1.0 - currentPrice / originalPrice) * 100
            // Làm tròn nếu gần như nguyên (ví dụ 10.0 -> 10)
            discountStr = formatter.format(initialDiscount)
        }
    }

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh Sửa chi tiết", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        // [LƯU]
                        val rawPrice = giaBanStr.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
                        val rawDiscount = discountStr.replace(",", ".").toDoubleOrNull() ?: 0.0

                        val updatedItem = selectedItem.copy(
                            soLuong = soLuong,
                            giaBan = rawPrice,       // Lưu giá gốc
                            chietKhau = rawDiscount  // Lưu % chiết khấu
                        )
                        orderViewModel.updateCartItem(updatedItem)
                        navController.popBackStack()
                    }) {
                        Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            Button(
                onClick = {
                    // 2. SỬA SỰ KIỆN CLICK: Thay vì xóa ngay, hãy hiện dialog
                    showDeleteDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9534F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Xóa", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Tên sản phẩm
            Text(
                text = selectedItem.tenMatHang,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Tồn kho
            Text(
                text = "(Kho còn: ${formatter.format(maxStock)})",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BỘ ĐẾM SỐ LƯỢNG ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = { if (soLuong > 1) soLuong-- },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Giảm", modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(24.dp))

                Text(
                    text = "$soLuong",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.width(24.dp))

                IconButton(
                    onClick = {
                        if (soLuong < maxStock) {
                            soLuong++
                        } else {
                            Toast.makeText(context, "Đã đạt giới hạn tồn kho!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tăng", modifier = Modifier.size(32.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Ô NHẬP GIÁ GỐC ---
            EditItemTextField(
                label = "Đơn giá gốc (đ)",
                value = giaBanStr,
                onValueChange = { giaBanStr = it }, // Chỉ lưu, không tính toán gì cả
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Ô NHẬP CHIẾT KHẤU (%) ---
            EditItemTextField(
                label = "Chiết khấu (%)",
                value = discountStr,
                onValueChange = { discountStr = it }, // Chỉ lưu, không tính toán
                keyboardType = KeyboardType.Number
            )

            // Hiển thị giá sau giảm để user dễ hình dung (Chỉ để xem)
            val price = giaBanStr.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
            val discount = discountStr.replace(",", ".").toDoubleOrNull() ?: 0.0
            val finalPrice = price * (1 - discount / 100)
            val formatterVND = DecimalFormat("#,###")

            Text(
                text = "Giá sau giảm: ${formatterVND.format(finalPrice)} đ",
                color = appBlueColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.End)
            )
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Xác nhận xóa") },
            text = { Text(text = "Bạn có chắc muốn xóa sản phẩm này khỏi đơn hàng không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Thực hiện xóa khi bấm "Đồng ý"
                        orderViewModel.removeProductFromCart(selectedItem.productId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Đồng ý", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Hủy", color = Color.Gray)
                }
            },
            containerColor = Color.White // Hoặc màu theo theme
        )
    }
}

@Composable
fun EditItemTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedBorderColor = Color.LightGray,
            focusedBorderColor = Color(0xFF0088FF)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}