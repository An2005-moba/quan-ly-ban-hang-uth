package com.nhom10.quanlybanhang.ui.screens.admin

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    // PROJECT ID của bạn (xem trong google-services.json hoặc file service_account.json)
    val projectId = "quan-ly-ban-hang-uth"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Panel - Gửi Thông Báo", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate(Routes.LOGIN) { popUpTo(0) }
                    }) {
                        Text("Đăng xuất")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Chức năng chỉ dành cho Admin", color = Color.Red)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tiêu đề thông báo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Nội dung thông báo") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    if (title.isBlank() || message.isBlank()) {
                        Toast.makeText(context, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                    } else {
                        isSending = true
                        sendFCMv1(context, projectId, title, message) { success, error ->
                            isSending = false
                            if (success) {
                                Toast.makeText(context, "Đã gửi thành công!", Toast.LENGTH_SHORT).show()
                                title = ""
                                message = ""
                            } else {
                                Toast.makeText(context, "Lỗi: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSending
            ) {
                if (isSending) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Gửi cho tất cả User")
            }
        }
    }
}

// Hàm xử lý gửi thông báo qua API v1 dùng Service Account
fun sendFCMv1(context: Context, projectId: String, title: String, body: String, onResult: (Boolean, String?) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 1. Đọc file JSON từ thư mục raw
            val inputStream = context.resources.openRawResource(R.raw.service_account)
            val googleCredentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/firebase.messaging"))

            googleCredentials.refreshIfExpired()
            val accessToken = googleCredentials.accessToken.tokenValue

            // 2. Tạo JSON Payload đúng chuẩn v1
            val json = JSONObject()
            val messageObj = JSONObject()
            val notificationObj = JSONObject()

            notificationObj.put("title", title)
            notificationObj.put("body", body)

            messageObj.put("topic", "all_users") // Gửi đến topic chung
            messageObj.put("notification", notificationObj)

            json.put("message", messageObj)

            // 3. Gọi API
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
                onResult(false, e.message)
            }
        }
    }
}