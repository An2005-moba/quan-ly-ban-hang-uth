package com.nhom10.quanlybanhang.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel
import com.nhom10.quanlybanhang.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel(),
    fontSizeViewModel: FontSizeViewModel

) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current
    val fontSize by fontSizeViewModel.fontSize.collectAsState()
    // Lấy trạng thái đăng nhập
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cài đặt", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background)  // DARK MODE
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SettingsOptionItem(
                    text = "Mật khẩu",
                    onClick = {
                        if (uiState.isGoogleLogin) {
                            Toast.makeText(
                                context,
                                "Tài khoản Google không cần đổi mật khẩu",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            navController.navigate(Routes.PASSWORD)
                        }
                    },
                    showArrow = !uiState.isGoogleLogin
                )

                SettingsOptionItem(
                    text = "Chủ đề",
                    onClick = { navController.navigate(Routes.THEME) },
                    showArrow = true
                )

                // Thêm mục Cỡ chữ
                SettingsOptionItem(
                    text = "Cỡ chữ",
                    fontSize = fontSize.sp,
                    onClick = { navController.navigate(Routes.FONT_SIZE) },
                    showArrow = true
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsOptionItem(
    text: String,
    onClick: () -> Unit,
    showArrow: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = MaterialTheme.typography.bodyLarge.fontSize
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface   // DARK MODE
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = fontSize),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )


            if (showArrow) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) // hợp dark mode
                )
            }
        }
    }
}

