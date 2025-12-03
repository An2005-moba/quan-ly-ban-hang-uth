package com.nhom10.quanlybanhang.ui.screens.admin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.R
import com.nhom10.quanlybanhang.Routes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationScreen(navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }

    // --- MÀU SẮC CHỦ ĐẠO CỦA APP (Lấy từ LoginScreen) ---
    val BluePrimary = Color(0xFF0088FF)
    val BackgroundColor = Color(0xFFF5F5F5) // Màu nền xám nhẹ cho sang
    val unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)

    // Cấu hình Project ID (Sửa lại nếu cần)
    val projectId = "quan-ly-ban-hang-uth"

    // Style cho TextField giống LoginScreen
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = unfocusedBorderColor,
        focusedBorderColor = BluePrimary,
        cursorColor = BluePrimary,
        focusedLabelColor = BluePrimary,
        focusedLeadingIconColor = BluePrimary,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Quản Trị Viên",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },

                actions = {
                    IconButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Routes.LOGIN) { popUpTo(0) }
                    }) {
                        Icon(Icons.Default.Logout, contentDescription = "Đăng xuất", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BluePrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon lớn trang trí
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(BluePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.NotificationsActive,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = BluePrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gửi Thông Báo Hệ Thống",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "Tin nhắn sẽ được gửi đến tất cả người dùng",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card chứa form nhập liệu
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Tiêu đề") },
                        placeholder = { Text("Ví dụ: Khuyến mãi sốc!") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                        colors = customTextFieldColors,
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Nội dung tin nhắn") },
                        placeholder = { Text("Nhập nội dung chi tiết...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp), // Tăng chiều cao cho nội dung
                        leadingIcon = {
                            Column(modifier = Modifier.height(120.dp), verticalArrangement = Arrangement.Top) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Icon(Icons.Default.Message, contentDescription = null)
                            }
                        },
                        colors = customTextFieldColors,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nút Gửi
            Button(
                onClick = {
                    if (title.isBlank() || message.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show()
                    } else {
                        isSending = true
                        sendFCMv1(context, projectId, title, message) { success, errorMsg ->
                            isSending = false
                            if (success) {
                                Toast.makeText(context, " Đã gửi thành công!", Toast.LENGTH_SHORT).show()
                                title = ""
                                message = ""
                            } else {
                                Toast.makeText(context, " Lỗi: $errorMsg", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSending
            ) {
                if (isSending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đang gửi...")
                } else {
                    Icon(Icons.Default.Send, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gửi Ngay", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- LOGIC GỬI THÔNG BÁO (GIỮ NGUYÊN) ---
fun sendFCMv1(context: Context, projectId: String, title: String, body: String, onResult: (Boolean, String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Kiểm tra file JSON tồn tại không để tránh Crash
            val resId = R.raw.service_account

            // 1. LẤY ACCESS TOKEN
            val inputStream = context.resources.openRawResource(resId)
            val googleCredentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"))

            googleCredentials.refreshIfExpired()
            val accessToken = googleCredentials.accessToken.tokenValue

            // 2. TẠO JSON PAYLOAD
            val json = JSONObject()
            val messageObj = JSONObject()
            val notificationObj = JSONObject()

            notificationObj.put("title", title)
            notificationObj.put("body", body)

            messageObj.put("topic", "all_users")
            messageObj.put("notification", notificationObj)

            json.put("message", messageObj)

            // 3. GỌI API
            val client = OkHttpClient()
            val requestBody = json.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/v1/projects/$projectId/messages:send")
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    onResult(true, null)
                } else {
                    onResult(false, "Code: ${response.code}, Body: $responseBody")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                onResult(false, "Lỗi: ${e.message} (Kiểm tra file service_account.json)")
            }
        }
    }
}