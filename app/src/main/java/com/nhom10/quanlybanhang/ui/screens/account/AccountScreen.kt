package com.nhom10.quanlybanhang.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes

@Composable
fun AccountScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Quay lại màu nền cố định (Xám nhạt)
            .background(Color(0xFFF0F2F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AccountOptionItem(
            icon = Icons.Default.Edit,
            text = "Chỉnh sửa thông tin cá nhân",
            onClick = {
                navController.navigate(Routes.EDIT_PROFILE)
            }
        )

        AccountOptionItem(
            icon = Icons.Default.Settings,
            text = "Cài đặt",
            onClick = {
                navController.navigate(Routes.SETTINGS)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AccountOptionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    val appBlueColor = Color(0xFF0088FF)

    // Không còn logic Dark Mode, Viền, hay đổi màu chữ nữa
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        // Quay lại màu nền thẻ cố định (Trắng)
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = appBlueColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                // Quay lại màu chữ mặc định (Đen)
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(navController = rememberNavController())
}