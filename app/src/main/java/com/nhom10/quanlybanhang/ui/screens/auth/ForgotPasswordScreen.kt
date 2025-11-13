package com.nhom10.quanlybanhang.ui.screens.auth

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.service.ForgotPasswordViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = viewModel()
) {
    val appBlueColor = Color(0xFF0088FF)
    var email by rememberSaveable { mutableStateOf("") }
    val labelColor = Color.Black.copy(alpha = 0.5f)
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val errorColor = MaterialTheme.colorScheme.error

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F2F5))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quên mật khẩu",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nhập địa chỉ email của bạn dưới đây. Nếu email này tồn tại " +
                    "trong hệ thống, mật khẩu mới được tạo ngẫu nhiên " +
                    "sẽ được gửi đến email đó.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = labelColor,
                unfocusedLabelColor = labelColor,
                errorBorderColor = errorColor,
                errorLabelColor = errorColor
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) {
                    Text(text = uiState.emailError!!, color = errorColor)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.sendResetLink(email)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = appBlueColor
            ),
            shape = RoundedCornerShape(4.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Gửi mật khẩu mới")
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text(
                text = "Quay lại đăng nhập",
                color = appBlueColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
    LaunchedEffect(key1 = uiState.result) {
        uiState.result?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (message == "Đã gửi link, vui lòng kiểm tra email!") {
                navController.popBackStack()
            }
            viewModel.resetResultState()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordScreenPreview() {
    ForgotPasswordScreen(navController = rememberNavController())
}