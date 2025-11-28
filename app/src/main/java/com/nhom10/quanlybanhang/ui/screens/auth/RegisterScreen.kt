package com.nhom10.quanlybanhang.ui.screens.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import com.nhom10.quanlybanhang.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    // --- State ---
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
                authViewModel.registerWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Lỗi Google: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

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

        Text("Đăng ký tài khoản", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = hoTen, onValueChange = { hoTen = it }, label = { Text("Họ Tên") }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = customTextFieldColors,
            isError = uiState.hoTenError != null, supportingText = { if (uiState.hoTenError != null) Text(uiState.hoTenError!!, color = errorColor) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true, colors = customTextFieldColors,
            isError = uiState.emailError != null, supportingText = { if (uiState.emailError != null) Text(uiState.emailError!!, color = errorColor) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = ngaySinh,
                onValueChange = { ngaySinh = it },
                label = { Text("Ngày sinh") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, "Chọn ngày sinh")
                    }
                },
                singleLine = true,
                colors = customTextFieldColors,
                isError = uiState.ngaySinhError != null,
                supportingText = { if (uiState.ngaySinhError != null) Text(uiState.ngaySinhError!!, color = errorColor) }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = matKhau, onValueChange = { matKhau = it }, label = { Text("Mật khẩu") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = if (matKhauAn) PasswordVisualTransformation() else VisualTransformation.None, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = { IconButton(onClick = { matKhauAn = !matKhauAn }) { Icon(if (matKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Ẩn/Hiện") } }, colors = customTextFieldColors,
            isError = uiState.matKhauError != null, supportingText = { if (uiState.matKhauError != null) Text(uiState.matKhauError!!, color = errorColor) }
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = xacNhanMatKhau, onValueChange = { xacNhanMatKhau = it }, label = { Text("Xác nhận mật khẩu") }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            visualTransformation = if (xacNhanMatKhauAn) PasswordVisualTransformation() else VisualTransformation.None, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = { IconButton(onClick = { xacNhanMatKhauAn = !xacNhanMatKhauAn }) { Icon(if (xacNhanMatKhauAn) Icons.Default.VisibilityOff else Icons.Default.Visibility, "Ẩn/Hiện") } }, colors = customTextFieldColors,
            isError = uiState.xacNhanMatKhauError != null, supportingText = { if (uiState.xacNhanMatKhauError != null) Text(uiState.xacNhanMatKhauError!!, color = errorColor) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                authViewModel.registerUser(email, matKhau, xacNhanMatKhau, hoTen, ngaySinh)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
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

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
            Text("HOẶC", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            Divider(modifier = Modifier.weight(1f), color = Color.Gray.copy(alpha = 0.3f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = {
                googleSignInClient.signOut().addOnCompleteListener {
                    googleAuthLauncher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.LightGray),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
        ) {
            Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google Logo", modifier = Modifier.size(30.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Đăng ký bằng Google", color = Color.Black, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                TextButton(onClick = {
                    showDatePicker = false
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDateMillis))
                        ngaySinh = formattedDate
                    }
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Hủy") } }
        ) { DatePicker(state = datePickerState) }
    }

    LaunchedEffect(key1 = uiState.registrationResult) {
        uiState.registrationResult?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            if (message == "Đăng ký thành công! Vui lòng đăng nhập.") {
                navController.popBackStack()
            }
            if (message == "Đăng nhập Google thành công!") {
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