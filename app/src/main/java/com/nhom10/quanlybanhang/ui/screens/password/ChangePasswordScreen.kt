package com.nhom10.quanlybanhang.ui.screens.password

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme // <-- Import kiểm tra Dark Mode
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.viewmodel.ChangePasswordViewModel // Đảm bảo import đúng ViewModel của bạn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = viewModel()
) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // === LOGIC DARK MODE ===
    val isDark = isSystemInDarkTheme()

    // Màu nền TopBar
    val topBarContainerColor = if (isDark) MaterialTheme.colorScheme.surface else appBlueColor
    val topBarContentColor = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White

    // Màu nền TextField
    val textFieldContainerColor = if (isDark) MaterialTheme.colorScheme.surface else Color.White

    // Màu nhãn (Label)
    val labelColor = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black.copy(alpha = 0.5f)

    val errorColor = MaterialTheme.colorScheme.error

    // Biến state cho các ô nhập
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrent by remember { mutableStateOf(false) }
    var showNew by remember { mutableStateOf(false) }
    var showConfirm by remember { mutableStateOf(false) }

    // Cấu hình màu sắc TextField
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = textFieldContainerColor,
        unfocusedContainerColor = textFieldContainerColor,
        unfocusedBorderColor = if (isDark) Color.Gray else Color.LightGray,
        focusedBorderColor = appBlueColor,
        focusedLabelColor = appBlueColor, // Màu label khi focus
        unfocusedLabelColor = labelColor, // Màu label khi không focus
        errorBorderColor = errorColor,
        errorLabelColor = errorColor,
        errorSupportingTextColor = errorColor,
        focusedTextColor = MaterialTheme.colorScheme.onSurface, // Màu chữ nhập vào
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface // Màu chữ nhập vào
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Mật khẩu", fontWeight = FontWeight.Bold)
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
                    containerColor = topBarContainerColor, // <-- Màu động
                    titleContentColor = topBarContentColor, // <-- Màu động
                    navigationIconContentColor = topBarContentColor
                )
            )
        },

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    // --- MÀU NỀN MÀN HÌNH ĐỘNG ---
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. MẬT KHẨU HIỆN TẠI ---
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Nhập mật khẩu hiện tại") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    visualTransformation = if (showCurrent) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showCurrent = !showCurrent }) {
                            Icon(
                                imageVector = if (showCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = labelColor
                            )
                        }
                    },
                    isError = uiState.currentPassError != null,
                    supportingText = {
                        if (uiState.currentPassError != null) {
                            Text(uiState.currentPassError!!)
                        }
                    }
                )

                // --- 2. MẬT KHẨU MỚI ---
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nhập mật khẩu mới") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    visualTransformation = if (showNew) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showNew = !showNew }) {
                            Icon(
                                imageVector = if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = labelColor
                            )
                        }
                    },
                    isError = uiState.newPassError != null,
                    supportingText = {
                        if (uiState.newPassError != null) {
                            Text(uiState.newPassError!!)
                        }
                    }
                )

                // --- 3. XÁC NHẬN MẬT KHẨU ---
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Xác nhận mật khẩu mới") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = textFieldColors,
                    singleLine = true,
                    visualTransformation = if (showConfirm) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { showConfirm = !showConfirm }) {
                            Icon(
                                imageVector = if (showConfirm) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = labelColor
                            )
                        }
                    },
                    isError = uiState.confirmPassError != null,
                    supportingText = {
                        if (uiState.confirmPassError != null) {
                            Text(uiState.confirmPassError!!)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- NÚT XÁC NHẬN ---
                Button(
                    onClick = {
                        viewModel.changePassword(currentPassword, newPassword, confirmPassword)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    // Màu nút động (Tối -> Xám đậm, Sáng -> Xanh)
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else appBlueColor
                    ),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = if (isDark) Color.White else Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            "Xác nhận đổi",
                            // Màu chữ nút động
                            color = if (isDark) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }
                }
            }
        }
    )

    // --- LẮNG NGHE KẾT QUẢ ---
    LaunchedEffect(key1 = uiState.result) {
        uiState.result?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (uiState.isSuccess) {
                navController.popBackStack()
            }
            viewModel.resetState()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen(navController = rememberNavController())
}