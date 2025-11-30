package com.nhom10.quanlybanhang.ui.screens.font

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeScreen(
    navController: NavController,
    fontSizeViewModel: FontSizeViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val appBlueColor = Color(0xFF0088FF)

    // Lấy giá trị fontSize hiện tại
    val currentFontSize by fontSizeViewModel.fontSize.collectAsState()
    var tempFontSize by remember { mutableStateOf(currentFontSize) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cỡ chữ", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Cỡ chữ hiện tại: ${tempFontSize.toInt()}sp",
                    fontSize = tempFontSize.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { tempFontSize = (tempFontSize - 2f).coerceAtLeast(8f) },
                        colors = ButtonDefaults.buttonColors(containerColor = appBlueColor)
                    ) {
                        Text("Giảm", color = Color.White)
                    }
                    Button(
                        onClick = { tempFontSize = (tempFontSize + 2f).coerceAtMost(36f) },
                        colors = ButtonDefaults.buttonColors(containerColor = appBlueColor)
                    ) {
                        Text("Tăng", color = Color.White)
                    }
                }

                // Nút lưu
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable {
                            fontSizeViewModel.setFontSize(tempFontSize)
                            coroutineScope.launch { fontSizeViewModel.saveFontSize() }
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = appBlueColor
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Lưu",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    )
}
