package com.nhom10.quanlybanhang.ui.screens.auth

import android.widget.Toast // <-- THÊM
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // <-- THÊM
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
// --- THÊM CÁC IMPORT ĐỂ KẾT NỐI BE ---
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.service.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    var email by rememberSaveable { mutableStateOf("") }
    var matKhau by rememberSaveable { mutableStateOf("") }
    var matKhauAn by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val uiState by loginViewModel.uiState.collectAsState()
    val errorColor = MaterialTheme.colorScheme.error

    val borderColor = Color(0xFF0088FF)
    val unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = unfocusedBorderColor,
        focusedBorderColor = borderColor,
        cursorColor = borderColor,
        focusedLabelColor = borderColor,
        unfocusedLabelColor = unfocusedBorderColor,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        errorBorderColor = errorColor,
        errorLabelColor = errorColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Đăng nhập",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
            },
            colors = customTextFieldColors,
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) {
                    Text(text = uiState.emailError!!, color = errorColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (matKhauAn) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {
                Icon(imageVector = Icons.Default.Lock, contentDescription = "Password")
            },
            trailingIcon = {
                val icon = if (matKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { matKhauAn = !matKhauAn }) {
                    Icon(imageVector = icon, contentDescription = "Hiện/Ẩn mật khẩu")
                }
            },
            colors = customTextFieldColors,
            isError = uiState.passwordError != null,
            supportingText = {
                if (uiState.passwordError != null) {
                    Text(text = uiState.passwordError!!, color = errorColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            TextButton(
                onClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Quên mật khẩu?", color = borderColor)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                loginViewModel.loginUser(email, matKhau)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = borderColor),
            shape = RoundedCornerShape(4.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Đăng nhập", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng ký
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Bạn chưa có tài khoản?")
            TextButton(
                onClick = { navController.navigate(Routes.REGISTER) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Đăng ký ngay", color = borderColor)
            }
        }
    }
    LaunchedEffect(key1 = uiState.loginResult) {
        uiState.loginResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            if (message == "Đăng nhập thành công!") {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            loginViewModel.resetLoginResult()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}