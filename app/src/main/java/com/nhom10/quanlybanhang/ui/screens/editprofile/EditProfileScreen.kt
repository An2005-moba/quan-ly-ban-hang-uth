package com.nhom10.quanlybanhang.ui.screens.editprofile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.service.EditProfileViewModel
import com.nhom10.quanlybanhang.ui.components.LetterAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel()
) {
    val appBlueColor = Color(0xFF0088FF)
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var showGenderDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    // --- 1. TẠO BỘ CHỌN ẢNH (PHOTO PICKER) ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        // Khi người dùng chọn xong ảnh, uri sẽ có giá trị
        if (uri != null) {
            // Gọi ViewModel để upload
            viewModel.uploadAvatar(uri)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chỉnh sửa thông tin cá nhân", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
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
                val placeholderPainter = rememberVectorPainter(image = Icons.Default.Person)

                // Mục 1: Ảnh đại diện
                EditProfileItem(
                    title = "Ảnh đại diện",
                    // --- 2. SỬA SỰ KIỆN CLICK ---
                    onClick = {
                        if (!uiState.isGoogleLogin) {
                            // Mở thư viện ảnh (Chỉ chọn ảnh)
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        } else {
                            Toast.makeText(context, "Tài khoản Google không thể đổi ảnh", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    // --- 3. HIỂN THỊ LOADING KHI ĐANG UPLOAD ---
                    if (uiState.isUploading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = appBlueColor
                        )
                    } else {
                        // Logic hiển thị ảnh bình thường
                        if (!uiState.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = uiState.photoUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                placeholder = placeholderPainter,
                                error = placeholderPainter
                            )
                        } else {
                            LetterAvatar(name = uiState.userName, size = 40.dp)
                        }
                    }

                    if (!uiState.isGoogleLogin) Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                // (Các mục Tên, Email, Giới tính, Nút Đăng xuất... giữ nguyên như cũ)
                EditProfileItem(
                    title = "Tên tài khoản",
                    onClick = { if (!uiState.isGoogleLogin) showNameDialog = true else Toast.makeText(context, "Tài khoản Google không thể đổi tên", Toast.LENGTH_SHORT).show() }
                ) {
                    Text(uiState.userName, color = Color.Gray)
                    if (!uiState.isGoogleLogin) Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                EditProfileItem(title = "Email", onClick = {}) {
                    Text(uiState.email, color = Color.Gray)
                }

                EditProfileItem(title = "Giới tính", onClick = { showGenderDialog = true }) {
                    Text(uiState.gender, color = Color.Gray)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        auth.signOut()
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                        googleSignInClient.signOut().addOnCompleteListener {
                            navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                        Text("Đăng xuất", color = Color.Black, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    )

    // (Các Dialog Sửa tên và Giới tính giữ nguyên như cũ)
    if (showNameDialog) {
        NameEditDialog(
            initialName = uiState.userName,
            onDismiss = { showNameDialog = false },
            onSave = { newName ->
                viewModel.updateName(newName)
                showNameDialog = false
            }
        )
    }

    if (showGenderDialog) {
        Dialog(onDismissRequest = { showGenderDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        IconButton(onClick = { showGenderDialog = false }, modifier = Modifier.align(Alignment.CenterStart)) {
                            Icon(Icons.Default.Close, contentDescription = "Đóng")
                        }
                        Text("Giới tính", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    GenderSelectionButton("Khác") { viewModel.updateGender("Khác"); showGenderDialog = false }
                    Spacer(modifier = Modifier.height(8.dp))
                    GenderSelectionButton("Nữ giới") { viewModel.updateGender("Nữ"); showGenderDialog = false }
                    Spacer(modifier = Modifier.height(8.dp))
                    GenderSelectionButton("Nam giới") { viewModel.updateGender("Nam"); showGenderDialog = false }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Tôi không muốn tiết lộ!", modifier = Modifier.clickable { viewModel.updateGender("Không tiết lộ"); showGenderDialog = false })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ... (Các hàm phụ trợ NameEditDialog, GenderSelectionButton, EditProfileItem giữ nguyên)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NameEditDialog(initialName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(initialName) }
    val maxLength = 50
    val appBlueColor = Color(0xFF0088FF)
    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Tên nick", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = name,
                    onValueChange = { if (it.length <= maxLength) name = it },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFFEEEEEE), unfocusedContainerColor = Color(0xFFEEEEEE), disabledContainerColor = Color(0xFFEEEEEE), focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent, disabledIndicatorColor = Color.Transparent),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    trailingIcon = { Text("${name.length}/$maxLength", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(end = 8.dp)) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = appBlueColor), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Close, "Hủy", tint = Color.White) }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(onClick = { onSave(name) }, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = appBlueColor), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Check, "Lưu", tint = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun GenderSelectionButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0088FF)), shape = RoundedCornerShape(12.dp)) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Medium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileItem(title: String, onClick: () -> Unit, trailingContent: @Composable RowScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().height(70.dp).clickable { onClick() }, shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) { trailingContent() }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(navController = rememberNavController())
}