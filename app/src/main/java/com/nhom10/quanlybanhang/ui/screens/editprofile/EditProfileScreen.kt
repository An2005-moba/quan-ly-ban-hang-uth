package com.nhom10.quanlybanhang.ui.screens.editprofile

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
import androidx.compose.ui.platform.LocalContext // <-- Cần Import này
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn // <-- Cần Import này
import com.google.android.gms.auth.api.signin.GoogleSignInOptions // <-- Cần Import này
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController
) {
    val appBlueColor = Color(0xFF0088FF)
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current // <-- Lấy context để dùng cho Google

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Chỉnh sửa thông tin cá nhân", fontWeight = FontWeight.Bold)
                },
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
                    .background(Color(0xFFF0F2F5))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val photoUrl = "URL_ANH_DAI_DIEN_CUA_BAN_TU_FIREBASE"
                val userName = "Võ Anh Quốc"
                val gender = "Nam"
                val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)

                EditProfileItem(
                    title = "Ảnh đại diện",
                    onClick = { /* TODO: Mở trình chọn ảnh */ }
                ) {
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

                EditProfileItem(
                    title = "Tên tài khoản",
                    onClick = { /* TODO: Mở dialog sửa tên */ }
                ) {
                    Text(userName, color = Color.Gray)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                EditProfileItem(
                    title = "Giới tính",
                    onClick = { /* TODO: Mở dialog chọn giới tính */ }
                ) {
                    Text(gender, color = Color.Gray)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- NÚT ĐĂNG XUẤT (ĐÃ NÂNG CẤP) ---
                Button(
                    onClick = {
                        // 1. Đăng xuất Firebase
                        auth.signOut()

                        // 2. Đăng xuất Google Client (Để lần sau nó hỏi lại tài khoản)
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)

                        googleSignInClient.signOut().addOnCompleteListener {
                            // 3. Chuyển về màn hình Login và xóa lịch sử
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        }
                    },
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