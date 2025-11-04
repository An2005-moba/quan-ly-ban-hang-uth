package com.nhom10.quanlybanhang.ui.screens.editprofile // Đảm bảo tên gói đúng

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
import coil.compose.AsyncImage // Nhớ import Coil

/**
 * Màn hình Chỉnh sửa thông tin cá nhân
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClicked: () -> Unit // Hàm để xử lý quay lại
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
                    IconButton(onClick = onBackClicked) { // Nhấn vào đây để quay lại
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
                // (Tôi dùng Button nhưng style cho nó giống Card)
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
                            color = Color.Black, // Bạn có thể đổi thành Color.Red
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
    // 'trailingContent' là phần bên phải (ảnh, chữ, mũi tên)
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
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), // Giảm padding dọc
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            // Hàng phụ cho nội dung bên phải
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
    EditProfileScreen(onBackClicked = {})
}