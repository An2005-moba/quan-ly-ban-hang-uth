package com.nhom10.quanlybanhang.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen() {

    // --- Biến State ---
    var hoTen by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var ngaySinh by remember { mutableStateOf("") }
    var matKhau by remember { mutableStateOf("") }
    var xacNhanMatKhau by remember { mutableStateOf("") }

    var matKhauAn by remember { mutableStateOf(true) }
    var xacNhanMatKhauAn by remember { mutableStateOf(true) }

    // --- Màu viền và nhãn ---
    val borderColor = Color(0xFF0088FF)
    val unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)

    val customTextFieldColors = TextFieldDefaults.colors(
        unfocusedIndicatorColor = unfocusedBorderColor,
        focusedIndicatorColor = borderColor,
        cursorColor = borderColor,
        focusedLabelColor = borderColor,
        unfocusedLabelColor = unfocusedBorderColor,
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.White,
        disabledContainerColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Tiêu đề
        Text(
            text = "Đăng ký tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Ô nhập Họ tên
        OutlinedTextField(
            value = hoTen,
            onValueChange = { hoTen = it },
            label = { Text("Họ Tên") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Ngày sinh
        OutlinedTextField(
            value = ngaySinh,
            onValueChange = { ngaySinh = it },
            label = { Text("Ngày sinh") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { /* TODO: Mở DatePicker */ }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Chọn ngày sinh"
                    )
                }
            },
            singleLine = true,
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Mật khẩu
        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (matKhauAn) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icon = if (matKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { matKhauAn = !matKhauAn }) {
                    Icon(imageVector = icon, contentDescription = "Hiện/Ẩn mật khẩu")
                }
            },
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Xác nhận mật khẩu
        OutlinedTextField(
            value = xacNhanMatKhau,
            onValueChange = { xacNhanMatKhau = it },
            label = { Text("Xác nhận mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (xacNhanMatKhauAn) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icon = if (xacNhanMatKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { xacNhanMatKhauAn = !xacNhanMatKhauAn }) {
                    Icon(imageVector = icon, contentDescription = "Hiện/Ẩn mật khẩu")
                }
            },
            colors = customTextFieldColors
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Nút Đăng ký
        Button(
            onClick = { /* TODO: Xử lý đăng ký */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = borderColor),
            shape = RoundedCornerShape(4.dp)
        ) {
            Text(text = "Đăng ký", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dòng chữ “Bạn đã có tài khoản?”
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Bạn đã có tài khoản?")
            TextButton(
                onClick = { /* TODO: chuyển sang màn hình đăng nhập */ },
                contentPadding = PaddingValues(1.dp)
            ) {
                Text(text = "Đăng nhập ngay", color = borderColor)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
