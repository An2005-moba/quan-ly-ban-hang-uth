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
import androidx.compose.runtime.* // Import quan trọng cho remember, mutableStateOf
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

    // [SỬA 1] Không dùng 'by', dùng .value để tránh lỗi "Cannot infer type"
    val selectedItemState = orderViewModel.selectedOrderItem.collectAsState()
    val selectedItem = selectedItemState.value

    // [SỬA 2] Lấy danh sách sản phẩm
    val productsState = productViewModel.products.collectAsState()
    val productList = productsState.value

    // Nếu null thì quay về, không render tiếp để tránh crash
    if (selectedItem == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        // Trả về một Box trống để placeholder trong khi chờ popBackStack
        Box(Modifier.fillMaxSize().background(scaffoldBgColor))
        return
    }

    // Logic tìm tồn kho (Dùng !! ở đây an toàn vì đã check null ở trên)
    val currentProduct = remember(selectedItem, productList) {
        productList.find { it.documentId == selectedItem.productId }
    }
    val maxStock = (currentProduct?.soLuong ?: 0.0)

    // [SỬA 3] Dùng mutableStateOf thay vì mutableIntStateOf để dễ chịu hơn với các phiên bản compose
    var soLuong by remember { mutableStateOf(selectedItem.soLuong) }

    val formatter = DecimalFormat("#")
    var giaBanStr by remember { mutableStateOf(formatter.format(selectedItem.giaBan)) }
    var note by remember { mutableStateOf("") }

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
                        // Logic Lưu
                        val newPrice = giaBanStr.replace(".", "").replace(",", "").toDoubleOrNull() ?: 0.0
                        val updatedItem = selectedItem.copy(
                            soLuong = soLuong,
                            giaBan = newPrice
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
                    orderViewModel.removeProductFromCart(selectedItem.productId)
                    navController.popBackStack()
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

            Text(
                text = selectedItem.tenMatHang,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "(Kho còn: ${formatter.format(maxStock)})",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Bộ tăng giảm số lượng
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

            EditItemTextField(
                label = "Giá bán",
                value = giaBanStr,
                onValueChange = { giaBanStr = it },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            EditItemTextField(
                label = "Chiết khấu mặt hàng",
                value = "0 %",
                onValueChange = { },
                keyboardType = KeyboardType.Number
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Ghi Chú") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }
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