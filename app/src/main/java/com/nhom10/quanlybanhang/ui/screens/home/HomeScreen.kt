package com.nhom10.quanlybanhang.ui.screens.home

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.viewmodel.AccountViewModel
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.ui.screens.account.AccountScreen
import com.nhom10.quanlybanhang.ui.screens.history.HistoryScreen
import com.nhom10.quanlybanhang.ui.screens.report.ReportScreen
import com.nhom10.quanlybanhang.ui.components.LetterAvatar
// --- CÁC IMPORT MỚI ĐỂ XỬ LÝ TẢI LẠI ---
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel

// Dữ liệu các mục trong thanh Bottom Nav
data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    orderViewModel: OrderViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    var selectedItemIndex by rememberSaveable { mutableIntStateOf(0) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val productList by productViewModel.products.collectAsState()
    // 2. LOGIC LỌC DANH SÁCH (Case-insensitive)
    // Nếu có từ khóa -> Lọc và hiển thị (sẽ tự động ở đầu danh sách). Nếu không -> Hiển thị hết.
    val filteredList = remember(productList, searchQuery) {
        if (searchQuery.isBlank()) {
            productList
        } else {
            productList.filter { product ->
                product.tenMatHang.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(key1 = true) {
        productViewModel.loadProducts()
    }

    LaunchedEffect(key1 = true) {
        productViewModel.loadProducts()
    }

    val bottomNavItems = listOf(
        BottomNavItem("Trang chủ", Icons.Default.Home),
        BottomNavItem("Báo cáo", Icons.Default.BarChart),
        BottomNavItem("Lịch sử GD", Icons.Default.CalendarMonth),
        BottomNavItem("Tài khoản", Icons.Default.Person)
    )

    Scaffold(
        topBar = {
            when (selectedItemIndex) {
                1 -> ReportTopBar(appBlueColor = appBlueColor)
                3 -> AccountTopBar(appBlueColor = appBlueColor)
                else -> DefaultTopBar(
                    title = when (selectedItemIndex) {
                        0 -> "Bán hàng"
                        2 -> "Lịch sử Giao dịch"
                        else -> "Bán hàng"
                    },
                    showShoppingCart = (selectedItemIndex == 0),
                    appBlueColor = appBlueColor,
                    navController = navController
                )
            }
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.PRODUCT_SETUP) },
                modifier = Modifier.offset(y = 45.dp).size(60.dp),
                containerColor = appBlueColor,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm", modifier = Modifier.size(32.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,

        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(85.dp),
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        bottomNavItems.take(2).forEachIndexed { index, item ->
                            BottomBarItem(item, selectedItemIndex == index, appBlueColor) { selectedItemIndex = index }
                        }
                    }
                    Spacer(modifier = Modifier.width(70.dp))
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                        bottomNavItems.drop(2).forEachIndexed { idx, item ->
                            val index = idx + 2
                            BottomBarItem(item, selectedItemIndex == index, appBlueColor) { selectedItemIndex = index }
                        }
                    }
                }
            }
        },

        content = { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedItemIndex) {
                    0 -> {
                        SearchSection(
                            appBlueColor = appBlueColor,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it }
                        )

                        if (filteredList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    if (searchQuery.isEmpty()) "Chưa có sản phẩm nào" else "Không tìm thấy sản phẩm",
                                    color = Color.Gray
                                )
                            }
                        } else {
                            ProductListForHome(products = filteredList, onProductClick = { })
                        }
                    }
                    1 -> ReportScreen()
                    2 -> HistoryScreen(navController = navController, orderViewModel = orderViewModel)
                    3 -> AccountScreen(navController = navController)
                }
            }
        }
    )
}

@Composable
fun ProductListForHome(products: List<Product>, onProductClick: (Product) -> Unit) {
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Fastfood)
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(products) { product ->
            Card(modifier = Modifier.fillMaxWidth().height(90.dp).clickable { onProductClick(product) }, elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    val imageBitmap = remember(product.imageData) { base64ToImageBitmap(product.imageData) }
                    Box(modifier = Modifier.aspectRatio(1f).fillMaxHeight().clip(RoundedCornerShape(8.dp)).background(Color.LightGray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        if (imageBitmap != null) {
                            Image(bitmap = imageBitmap, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        } else {
                            Icon(painter = placeholderPainter, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(24.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                        Text(product.tenMatHang, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                        Text("Còn: ${product.soLuong} ${product.donViTinh}", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    }
                    Text("${product.giaBan} đ", fontWeight = FontWeight.Bold, color = Color(0xFF0088FF))
                }
            }
        }
    }
}

private fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    if (base64String.isEmpty()) return null
    return try {
        val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)?.asImageBitmap()
    } catch (e: Exception) { null }
}

// === ACCOUNT TOP BAR CÓ LOGIC TỰ LÀM MỚI ===
@Composable
private fun AccountTopBar(
    appBlueColor: Color,
    viewModel: AccountViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)
    val currentPhotoUrl = uiState.photoUrl

    // --- PHẦN MỚI THÊM: TỰ ĐỘNG TẢI LẠI DỮ LIỆU KHI MÀN HÌNH HIỆN ---
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Khi quay lại màn hình này (ON_RESUME), gọi hàm tải lại dữ liệu
                viewModel.loadUserData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // ----------------------------------------------------------------

    Column(modifier = Modifier.fillMaxWidth().background(appBlueColor).statusBarsPadding().padding(horizontal = 20.dp, vertical = 24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (!currentPhotoUrl.isNullOrBlank()) {
                if (currentPhotoUrl.startsWith("http")) {
                    AsyncImage(model = currentPhotoUrl, contentDescription = "Avatar", modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White), contentScale = ContentScale.Crop, placeholder = placeholderPainter, error = placeholderPainter)
                } else {
                    val bitmap = remember(currentPhotoUrl) { base64ToImageBitmap(currentPhotoUrl) }
                    if (bitmap != null) {
                        Image(bitmap = bitmap, contentDescription = "Avatar", modifier = Modifier.size(64.dp).clip(CircleShape).background(Color.White), contentScale = ContentScale.Crop)
                    } else {
                        LetterAvatar(name = uiState.userName, size = 64.dp)
                    }
                }
            } else {
                LetterAvatar(name = uiState.userName, size = 64.dp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = uiState.userName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(text = uiState.userId, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun BottomBarItem(item: BottomNavItem, isSelected: Boolean, selectedColor: Color, onClick: () -> Unit) {
    val color = if (isSelected) selectedColor else Color.Gray
    Column(modifier = Modifier.padding(vertical = 8.dp).clickable { onClick() }, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(item.icon, contentDescription = null, tint = color)
        Text(text = item.label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopBar(appBlueColor: Color) {
    Column(modifier = Modifier.fillMaxWidth().background(appBlueColor).statusBarsPadding().padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Báo cáo", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp))
        Card(modifier = Modifier.padding(horizontal = 16.dp).height(48.dp), shape = CircleShape, colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                FilterButtonContent("Hôm nay", Modifier.weight(1f).clickable { /* TODO */ })
                VerticalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                FilterButtonContent("Tất cả", Modifier.weight(1f).clickable { /* TODO */ })
            }
        }
    }
}

@Composable
private fun FilterButtonContent(text: String, modifier: Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = Color(0xFF007AFF), fontWeight = FontWeight.Bold)
        Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF007AFF))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(title: String, showShoppingCart: Boolean, appBlueColor: Color, navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        actions = { if (showShoppingCart) IconButton(onClick = { navController.navigate(Routes.CART) }) { Icon(Icons.Default.ShoppingCart, null, tint = Color.White) } },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = appBlueColor, titleContentColor = Color.White, actionIconContentColor = Color.White)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(
    appBlueColor: Color,
    searchQuery: String,                  // Tham số mới
    onSearchQueryChange: (String) -> Unit // Tham số mới
) {
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange, // Sử dụng callback
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        placeholder = { Text("Tìm kiếm mặt hàng", color = appBlueColor) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = appBlueColor) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = appBlueColor, unfocusedBorderColor = lightGrayBorder)
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() { }