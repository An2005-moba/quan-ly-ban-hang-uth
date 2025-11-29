package com.nhom10.quanlybanhang.ui.screens.productsetup
import java.text.DecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
// Import các icon bạn cần
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Fastfood // Dùng icon này cho "Cá"
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel // Thêm import
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import com.nhom10.quanlybanhang.data.model.Product
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSetupScreen(
    navController: NavController,
    productViewModel: ProductViewModel // Sửa: Nhận ViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

    // Sửa: Lấy danh sách từ ViewModel
    val productList by productViewModel.products.collectAsState()

    // Sửa: Tải dữ liệu khi màn hình khởi chạy
    LaunchedEffect(key1 = true) {
        productViewModel.loadProducts()
    }

    // Xóa: Dữ liệu mẫu
    // val sampleProducts = listOf(...)

    Scaffold(
        // === 1. TOP BAR (Giữ nguyên) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thiết lập mặt hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.ADD_PRODUCT) }) {
                        Icon(Icons.Default.Add, "Thêm mặt hàng")
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

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5))
            ) {
                // Thanh tìm kiếm (Giữ nguyên)
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
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

                // Sửa: Dùng productList từ ViewModel
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(1.dp) // Ngăn cách mỏng
                ) {

                    val filteredList = productList.filter {
                        it.tenMatHang.contains(searchQuery, ignoreCase = true)
                    }

                    items(filteredList) { product ->
                        ProductListItem( // Truyền cả đối tượng product
                            product = product,
                            onClick = {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("product", product)

                                navController.navigate(Routes.EDIT_PRODUCT)
                            }

                        )
                    }
                }
            }
        }
    )
}

// Xóa: Data class ProductItem (vì đã dùng Product model)

/**
 * Composable phụ trợ cho một item trong danh sách
 */
val formatter = DecimalFormat("#,###")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListItem(
    product: Product, // Nhận cả đối tượng Product
    onClick: () -> Unit
) {
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Fastfood)

    ListItem(
        modifier = Modifier
            .background(Color.White)
            .clickable { onClick() },
        leadingContent = {
            // === LOGIC HIỂN THỊ ẢNH (COPY TỪ HOME) ===
            val imageBitmap = remember(product.imageData) {
                base64ToImageBitmap(product.imageData)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = product.tenMatHang,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = placeholderPainter,
                        contentDescription = "Placeholder",
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // ==========================================
        },
        headlineContent = { Text(product.tenMatHang, fontWeight = FontWeight.Bold) },
        supportingContent = {
            val details = "Giá: ${formatter.format(product.giaBan)} đ - Còn: ${formatter.format(product.soLuong)} ${product.donViTinh}"
            Text(details)
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent) // Nền trong suốt
    )
}

// === THÊM HÀM NÀY (COPY TỪ HOME) ===
private fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    if (base64String.isEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size).asImageBitmap()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true)
@Composable
fun ProductSetupScreenPreview() {
    // ProductSetupScreen(navController = rememberNavController(), ...)
}