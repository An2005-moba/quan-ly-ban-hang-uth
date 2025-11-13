package com.nhom10.quanlybanhang.ui.screens.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.service.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var hoTen by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var ngaySinh by rememberSaveable { mutableStateOf("") }
    var matKhau by rememberSaveable { mutableStateOf("") }
    var xacNhanMatKhau by rememberSaveable { mutableStateOf("") }
    var matKhauAn by remember { mutableStateOf(true) }
    var xacNhanMatKhauAn by remember { mutableStateOf(true) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    val errorColor = MaterialTheme.colorScheme.error
    val borderColor = Color(0xFF0088FF)
    val unfocusedBorderColor = Color.Black.copy(alpha = 0.2f)
    val customTextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedBorderColor = unfocusedBorderColor,
        focusedBorderColor = borderColor,
        cursorColor = borderColor,
        focusedLabelColor = borderColor,
        unfocusedLabelColor = unfocusedBorderColor,
        unfocusedContainerColor = Color.White,
        focusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        errorBorderColor = errorColor,
        errorLabelColor = errorColor
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Đăng ký tài khoản",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = hoTen,
            onValueChange = { hoTen = it },
            label = { Text("Họ Tên") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = customTextFieldColors,
            isError = uiState.hoTenError != null,
            supportingText = {
                if (uiState.hoTenError != null) {
                    Text(text = uiState.hoTenError!!, color = errorColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
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
            value = ngaySinh,
            onValueChange = { },
            readOnly = true,
            label = { Text("Ngày sinh") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Chọn ngày sinh"
                    )
                }
            },
            singleLine = true,
            colors = customTextFieldColors,
            isError = uiState.ngaySinhError != null,
            supportingText = {
                if (uiState.ngaySinhError != null) {
                    Text(text = uiState.ngaySinhError!!, color = errorColor)
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
            trailingIcon = {
                val icon = if (matKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { matKhauAn = !matKhauAn }) {
                    Icon(imageVector = icon, contentDescription = "Hiện/Ẩn mật khẩu")
                }
            },
            colors = customTextFieldColors,
            isError = uiState.matKhauError != null,
            supportingText = {
                if (uiState.matKhauError != null) {
                    Text(text = uiState.matKhauError!!, color = errorColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
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
            colors = customTextFieldColors,
            isError = uiState.xacNhanMatKhauError != null,
            supportingText = {
                if (uiState.xacNhanMatKhauError != null) {
                    Text(text = uiState.xacNhanMatKhauError!!, color = errorColor)
                }
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                authViewModel.registerUser(
                    email = email,
                    matKhau = matKhau,
                    xacNhanMatKhau = xacNhanMatKhau,
                    hoTen = hoTen,
                    ngaySinh = ngaySinh
                )
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
                Text(text = "Đăng ký", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng nhập
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Bạn đã có tài khoản?")
            TextButton(
                onClick = { navController.popBackStack() },
                contentPadding = PaddingValues(start = 4.dp)
            ) {
                Text(text = "Đăng nhập ngay", color = borderColor)
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(Date(selectedDateMillis))
                            ngaySinh = formattedDate
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Hủy") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LaunchedEffect(key1 = uiState.registrationResult) {
        uiState.registrationResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            if (message == "Đăng ký thành công!") {
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            authViewModel.resetRegistrationResult()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(navController = rememberNavController())
}