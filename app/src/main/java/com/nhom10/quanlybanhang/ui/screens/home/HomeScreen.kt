package com.nhom10.quanlybanhang.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nhom10.quanlybanhang.ui.screens.report.ReportScreen
import com.nhom10.quanlybanhang.ui.screens.account.AccountScreen
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.Routes

// Dữ liệu các mục trong thanh Bottom Nav
data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) { // <-- NHẬN NAVCONTROLLER
    val appBlueColor = Color(0xFF0088FF)
    var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("Trang chủ", Icons.Default.Home),
        BottomNavItem("Báo cáo", Icons.Default.BarChart),
        BottomNavItem("Lịch sử GD", Icons.Default.CalendarMonth),
        BottomNavItem("Tài khoản", Icons.Default.Person)
    )

    Scaffold(
        // === 1. TOP BAR ===
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

        // === 2. NÚT DẤU CỘNG (FAB) ===
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.PRODUCT_SETUP) },
                modifier = Modifier
                    .offset(y = 50.dp)
                    .size(60.dp),
                containerColor = appBlueColor,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp,
                    pressedElevation = 15.dp
                )
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Trái: 2 item đầu
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        bottomNavItems.take(2).forEachIndexed { index, item ->
                            BottomBarItem(
                                item = item,
                                isSelected = selectedItemIndex == index,
                                selectedColor = appBlueColor,
                                onClick = { selectedItemIndex = index }
                            )
                        }
                    }

                    // Khoảng trống giữa cho FAB
                    Spacer(modifier = Modifier.width(70.dp))

                    // Phải: 2 item cuối
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        bottomNavItems.drop(2).forEachIndexed { idx, item ->
                            val index = idx + 2
                            BottomBarItem(
                                item = item,
                                isSelected = selectedItemIndex == index,
                                selectedColor = appBlueColor,
                                onClick = { selectedItemIndex = index }
                            )
                        }
                    }
                }
            }
        },

        // === 4. NỘI DUNG CHÍNH (ĐÃ SỬA CASE 3) ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                when (selectedItemIndex) {
                    0 -> {
                        SearchSection(appBlueColor = appBlueColor)
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            Text("Nội dung Trang chủ")
                        }
                    }
                    1 -> ReportScreen()
                    2 -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Nội dung Lịch sử GD") }
                    // TRUYỀN NAVCONTROLLER VÀO ACCOUNTSCREEN
                    3 -> AccountScreen(navController = navController)
                }
            }
        }
    )
}

// ... (Hàm AccountTopBar) ...
@Composable
private fun AccountTopBar(appBlueColor: Color) {
    val photoUrl = "URL_ANH_DAI_DIEN_CUA_BAN_TU_FIREBASE"
    val userName = "Võ Anh Quốc"
    val userId = "ID:12"
    val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appBlueColor)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentScale = ContentScale.Crop,
                placeholder = placeholderPainter,
                error = placeholderPainter
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = userName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = userId,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// ... (Tất cả các hàm private khác: BottomBarItem, ReportTopBar, FilterButtonContent, DefaultTopBar, SearchSection) ...
// (Tôi sẽ rút gọn chúng ở đây để tiết kiệm diện tích, nhưng bạn hãy giữ nguyên chúng trong code của mình)
@Composable
private fun BottomBarItem(item: BottomNavItem, isSelected: Boolean, selectedColor: Color, onClick: () -> Unit) {
    val color = if (isSelected) selectedColor else Color.Gray
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(vertical = 8.dp).clickable { onClick() }
    ) {
        Icon(item.icon, contentDescription = item.label, tint = color)
        Text(text = item.label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportTopBar(appBlueColor: Color) {
    Column(
        modifier = Modifier.fillMaxWidth().background(appBlueColor).statusBarsPadding().padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Báo cáo", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(vertical = 16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                FilterButtonContent(text = "Hôm nay", modifier = Modifier.weight(1f).clickable { /* TODO */ })
                VerticalDivider(modifier = Modifier.fillMaxHeight().width(1.dp), color = Color.LightGray.copy(alpha = 0.5f))
                FilterButtonContent(text = "Tất cả", modifier = Modifier.weight(1f).clickable { /* TODO */ })
            }
        }
    }
}

@Composable
private fun FilterButtonContent(text: String, modifier: Modifier) {
    Row(
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = Color(0xFF007AFF), fontWeight = FontWeight.Bold)
        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF007AFF))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultTopBar(title: String, showShoppingCart: Boolean, appBlueColor: Color,navController: NavController) {
    CenterAlignedTopAppBar(
        title = { Text(title, fontWeight = FontWeight.Bold) },
        actions = {
            if (showShoppingCart) {
                IconButton(onClick = { navController.navigate(Routes.CART)}) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = appBlueColor,
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchSection(appBlueColor: Color) {
    var searchQuery by remember { mutableStateOf("") }
    val lightGrayBorder = Color.Black.copy(alpha = 0.2f)
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        placeholder = { Text("Tìm kiếm mặt hàng", color = appBlueColor) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = appBlueColor) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = appBlueColor,
            unfocusedBorderColor = lightGrayBorder,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = appBlueColor,
            unfocusedTextColor = appBlueColor
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    // Sửa Preview để nó hoạt động (cần NavController giả)
    // Hoặc đơn giản là comment nó đi nếu nó gây lỗi
    // HomeScreen(navController = rememberNavController())
}