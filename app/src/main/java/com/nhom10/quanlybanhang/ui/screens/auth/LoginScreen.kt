package com.nhom10.quanlybanhang.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nhom10.quanlybanhang.R
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

    // Cấu hình Google
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleAuthLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                loginViewModel.loginWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign In thất bại: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

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

        // Ô nhập Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
            colors = customTextFieldColors,
            isError = uiState.emailError != null,
            supportingText = {
                if (uiState.emailError != null) Text(text = uiState.emailError!!, color = errorColor)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ô nhập Mật khẩu
        OutlinedTextField(
            value = matKhau,
            onValueChange = { matKhau = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (matKhauAn) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
            trailingIcon = {
                val icon = if (matKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { matKhauAn = !matKhauAn }) {
                    Icon(imageVector = icon, contentDescription = "Ẩn/Hiện")
                }
            },
            colors = customTextFieldColors,
            isError = uiState.passwordError != null,
            supportingText = {
                if (uiState.passwordError != null) Text(text = uiState.passwordError!!, color = errorColor)
            }
        )

        // Quên mật khẩu
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(
                onClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("Quên mật khẩu?", color = borderColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút Đăng nhập (Email)
        Button(
            onClick = { loginViewModel.loginUser(email, matKhau) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
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

        // Dòng kẻ HOẶC
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
            Text(
                text = "HOẶC",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
            Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút Đăng nhập Google
        OutlinedButton(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    googleAuthLauncher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "Google Logo",
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Đăng nhập bằng Google",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

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