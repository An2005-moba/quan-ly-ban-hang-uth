package com.nhom10.quanlybanhang.ui.screens.editprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
// --- THÊM 2 IMPORT NÀY ---
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


/**
 * Màn hình Chỉnh sửa thông tin cá nhân
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController // <-- SỬA LẠI: NHẬN NAVCONTROLLER
) {
    val appBlueColor = Color(0xFF0088FF)

    Scaffold(
        // === 1. TOP BAR (CÓ NÚT QUAY LẠI) ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chỉnh sửa thông tin cá nhân", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    // SỬA LẠI: Dùng navController để quay lại
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

        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5)) // Màu nền xám nhạt
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Dữ liệu mẫu (sẽ lấy từ ViewModel) ---
                val photoUrl = "URL_ANH_DAI_DIEN_CUA_BAN_TU_FIREBASE"
                val userName = "Võ Anh Quốc"
                val gender = "Nam"
                val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)

                // Mục 1: Ảnh đại diện
                EditProfileItem(
                    title = "Ảnh đại diện",
                    onClick = { /* TODO: Mở trình chọn ảnh */ }
                ) { // Đây là phần nội dung bên phải
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        placeholder = placeholderPainter,
                        error = placeholderPainter
                    )
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                // Mục 2: Tên tài khoản
                EditProfileItem(
                    title = "Tên tài khoản",
                    onClick = { /* TODO: Mở dialog sửa tên */ }
                ) {
                    Text(userName, color = Color.Gray)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                // Mục 3: Giới tính
                EditProfileItem(
                    title = "Giới tính",
                    onClick = { /* TODO: Mở dialog chọn giới tính */ }
                ) {
                    Text(gender, color = Color.Gray)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Mục 4: Nút Đăng xuất
                Button(
                    onClick = { /* TODO: Xử lý đăng xuất */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Đăng xuất",
                            color = Color.Black,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    )
}

/**
 * Composable phụ trợ cho một mục tùy chọn
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileItem(
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable RowScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                trailingContent()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(navController = rememberNavController())
}