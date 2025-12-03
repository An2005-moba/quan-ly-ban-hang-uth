package com.nhom10.quanlybanhang.ui.screens.customer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import android.util.Base64
import com.nhom10.quanlybanhang.utils.SearchHelper
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrderItemScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)
    val context = LocalContext.current

    // Lấy dữ liệu sản phẩm gốc
    val productList by productViewModel.products.collectAsState()
    // Lấy dữ liệu giỏ hàng để tính toán tồn kho còn lại
    val cartItems by orderViewModel.cartItems.collectAsState()

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Thêm vào đơn hàng", fontWeight = FontWeight.Bold) },
                actions = {
                    // Badge Giỏ hàng
                    BadgedBox(
                        badge = {
                            val count = cartItems.sumOf { it.soLuong }
                            if (count > 0) {
                                Badge(
                                    containerColor = Color.Red,
                                    contentColor = Color.White
                                ) { Text(count.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Default.ShoppingCart, "Giỏ hàng", tint = Color.White)
                    }
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Xong", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    placeholder = { Text("Tìm kiếm mặt hàng") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        unfocusedBorderColor = lightGrayBorder,
                        focusedBorderColor = appBlueColor
                    )
                )

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    val filteredList = productList.filter {
                        // SỬA ĐOẠN NÀY:
                        SearchHelper.isMatch(it.tenMatHang, searchQuery)
                    }

                    items(filteredList) { product ->

                        val itemInCart = cartItems.find { it.productId == product.documentId }
                        val quantityInCart = itemInCart?.soLuong ?: 0

                        // 2. Tính số lượng còn lại để hiển thị
                        // (Tổng kho gốc - Số đã nằm trong giỏ)
                        val remainingStock = product.soLuong - quantityInCart

                        ProductListItem(
                            product = product,
                            displayStock = remainingStock, // Truyền số lượng đã tính toán xuống UI
                            onClick = {
                                // 3. Kiểm tra logic khi click
                                if (remainingStock >= 1) {
                                    // Còn hàng -> Thêm vào giỏ (OrderViewModel tự xử lý cộng dồn)
                                    orderViewModel.addProductToCart(product)
                                } else {
                                    // Hết hàng (trên giao diện) -> Báo lỗi
                                    Toast.makeText(context, "Đã hết mặt hàng này!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}

/**
 * Item hiển thị: Đơn giản, không có nút +/-, chỉ hiển thị thông tin
 */
val formatter = DecimalFormat("#,###")

@Composable
private fun ProductListItem(
    product: Product,
    displayStock: Double, // Tham số mới: Số lượng kho để hiển thị
    onClick: () -> Unit
) {
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Fastfood)
    val imageBitmap = remember(product.imageData) {
        base64ToImageBitmap(product.imageData)
    }

    // Nếu hết hàng (displayStock <= 0), làm mờ item đi một chút để dễ nhận biết
    val backgroundColor = if (displayStock > 0) Color.White else Color(0xFFF0F0F0)
    val textColor = if (displayStock > 0) Color.Black else Color.Gray

    ListItem(
        modifier = Modifier
            .background(backgroundColor)
            .clickable { onClick() }, // Click vào toàn bộ dòng
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(painter = placeholderPainter, contentDescription = null, tint = Color.Gray)
                }
            }
        },
        headlineContent = {
            Text(product.tenMatHang, fontWeight = FontWeight.Bold, color = textColor)
        },
        supportingContent = {
            // Hiển thị giá và số lượng kho ĐÃ TRỪ
            val stockText = if (displayStock > 0) {
                formatter.format(displayStock)
            } else {
                "Hết hàng"
            }

            Text(
                "Giá: ${formatter.format(product.giaBan)} đ - Kho: $stockText ${product.donViTinh}",
                color = if (displayStock > 0) Color.Gray else Color.Red
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
    Divider(color = Color.LightGray.copy(alpha = 0.2f))
}

private fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    if (base64String.isEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
    } catch (e: Exception) { null }
}