package com.nhom10.quanlybanhang.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class BottomNavItem(val label: String, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val appBlueColor = Color(0xFF0088FF)
    var selectedItemIndex by remember { mutableStateOf(0) }

    val bottomNavItems = listOf(
        BottomNavItem("Trang chủ", Icons.Default.Home),
        BottomNavItem("Báo cáo", Icons.Default.BarChart),
        BottomNavItem("Lịch sử GD", Icons.Default.CalendarMonth),
        BottomNavItem("Tài khoản", Icons.Default.Person)
    )

    val fabDiameter = 56.dp
    val notchDiameter = fabDiameter + 14.dp

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bán hàng", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO */ },
                modifier = Modifier.offset(y = 54.dp),
                containerColor = appBlueColor,
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Thêm")
            }
        },
        floatingActionButtonPosition = FabPosition.Center,

        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                shadowElevation = 8.dp,
                color = Color.Transparent
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Vẽ bottom bar với notch
                    Canvas(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        val path = Path().apply {
                            val width = size.width
                            val height = size.height

                            val fabRadius = 28.dp.toPx()
                            val notchRadius = fabRadius + 8.dp.toPx()
                            val notchDepth = 45.dp.toPx()
                            val centerX = width / 2f

                            moveTo(0f, 0f)
                            lineTo(centerX - notchRadius - 8.dp.toPx(), 0f)

                            quadraticBezierTo(
                                centerX - notchRadius - 4.dp.toPx(), 0f,
                                centerX - notchRadius, 4.dp.toPx()
                            )

                            arcTo(
                                rect = Rect(
                                    left = centerX - notchRadius,
                                    top = -notchDepth,
                                    right = centerX + notchRadius,
                                    bottom = notchDepth
                                ),
                                startAngleDegrees = 180f,
                                sweepAngleDegrees = -180f,
                                forceMoveTo = false
                            )

                            quadraticBezierTo(
                                centerX + notchRadius + 4.dp.toPx(), 0f,
                                centerX + notchRadius + 8.dp.toPx(), 0f
                            )

                            lineTo(width, 0f)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }

                        drawPath(
                            path = path,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }

                    // Các icon bottom navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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

                        Spacer(modifier = Modifier.width(notchDiameter))

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
            }
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                var searchQuery by remember { mutableStateOf("") }
                val lightGrayBorder = Color.Black.copy(alpha = 0.2f)

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = { Text("Tìm kiếm mặt hàng", color = appBlueColor) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = appBlueColor)
                    },
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

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    when (selectedItemIndex) {
                        0 -> Text("Trang chủ", color = appBlueColor)
                        1 -> Text("Báo cáo", color = appBlueColor)
                        2 -> Text("Lịch sử GD", color = appBlueColor)
                        3 -> Text("Tài khoản", color = appBlueColor)
                    }
                }
            }
        }
    )
}

@Composable
fun BottomBarItem(
    item: BottomNavItem,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) selectedColor else Color.Gray
        )
        Text(
            text = item.label,
            color = if (isSelected) selectedColor else Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}