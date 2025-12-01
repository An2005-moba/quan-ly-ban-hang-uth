package com.nhom10.quanlybanhang.ui.screens.customer

// --- CÁC IMPORT BỊ THIẾU ---
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.nhom10.quanlybanhang.data.model.Customer
import com.nhom10.quanlybanhang.viewmodel.CustomerViewModel
import coil.compose.AsyncImage
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.rememberLauncherForActivityResult // Import này
import androidx.activity.result.PickVisualMediaRequest // Import này
import androidx.activity.result.contract.ActivityResultContracts // Import này
import androidx.compose.foundation.Image // Import này
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(
    navController: NavController,
    customerViewModel: CustomerViewModel // SỬA: Nhận ViewModel
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5) // Màu nền xám nhạt
    val textFieldBgColor = Color(0xFFE0E0E0) // Màu nền ô nhập

    // Biến state cho các trường
    var tenKhachHang by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") } // Đây là SĐT
    var diaChi by remember { mutableStateOf("") }
    var ghiChu by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current // THÊM
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri
    }
    Scaffold(
        containerColor = scaffoldBgColor,
        // === 1. TOP BAR ===
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Thêm khách hàng", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        // SỬA: Logic nút Lưu
                        if (tenKhachHang.isBlank() || email.isBlank()) {
                            Toast.makeText(context, "Tên và SĐT là bắt buộc", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        val avatarString = if (imageUri != null) {
                            uriToBase64(context, imageUri!!)
                        } else {
                            ""
                        }
                        val newCustomer = Customer(
                            tenKhachHang = tenKhachHang,
                            soDienThoai = email, // Giả sử email là SĐT
                            diaChi = diaChi,
                            ghiChu = ghiChu,
                            avatarUrl = avatarString
                            // Bạn có thể thêm logic upload ảnh và lấy avatarUrl ở đây
                        )

                        customerViewModel.addCustomer(
                            customer = newCustomer,
                            onSuccess = {
                                Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            },
                            onFailure = { e ->
                                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }) {
                        Text("Lưu", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },

        // === 2. NỘI DUNG CHÍNH ===
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- Khối 1: Upload ảnh ---
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(textFieldBgColor)
                        .clickable {
                            // Mở trình chọn ảnh
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        // Hiển thị ảnh đã chọn
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Icon mặc định
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Thêm ảnh",
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                // --- Khối 2: Các trường thông tin ---
                CustomTextField(
                    value = tenKhachHang,
                    onValueChange = { tenKhachHang = it },
                    placeholder = "Tên khách hàng",
                    backgroundColor = textFieldBgColor
                )
                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Số điện thoại", // SỬA: Đổi placeholder
                    backgroundColor = textFieldBgColor
                )
                CustomTextField(
                    value = diaChi,
                    onValueChange = { diaChi = it },
                    placeholder = "Địa chỉ",
                    backgroundColor = textFieldBgColor
                )
                CustomTextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    placeholder = "Ghi chú",
                    backgroundColor = textFieldBgColor,
                    modifier = Modifier.height(150.dp) // Ô ghi chú cao hơn
                )
            }
        }
    )
}
private fun uriToBase64(context: Context, uri: Uri): String {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)

        // Resize ảnh để tránh chuỗi quá dài gây lỗi Firebase (giới hạn ~1MB)
        val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream) // Nén JPEG 70%
        val bytes = outputStream.toByteArray()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

@Composable
private fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = backgroundColor,
            focusedIndicatorColor = Color.Transparent, // Tắt gạch chân
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = false // Cho phép nhiều dòng (cho Ghi chú)
    )
}

