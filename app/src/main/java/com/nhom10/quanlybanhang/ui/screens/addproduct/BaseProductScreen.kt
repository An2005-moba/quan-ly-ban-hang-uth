package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // Thêm
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
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
import com.nhom10.quanlybanhang.data.model.Product // Đảm bảo đã import Product
import android.net.Uri // Thêm
import androidx.activity.compose.rememberLauncherForActivityResult // Thêm
import androidx.activity.result.contract.ActivityResultContracts // Thêm
import androidx.compose.ui.layout.ContentScale // Thêm
import coil.compose.AsyncImage // Thêm
import android.util.Base64 // Thêm
import androidx.compose.ui.platform.LocalContext // Thêm
import android.content.Context // Thêm
import java.io.ByteArrayOutputStream // Thêm
import androidx.compose.ui.unit.sp // Thêm
import android.graphics.BitmapFactory // <--- THÊM
import android.graphics.Bitmap

/**
 * Hàm chuyển đổi Uri ảnh thành chuỗi Base64
 */
private fun uriToBase64(context: Context, uri: Uri): String {
    return try {

        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()
        if (bitmap == null) return ""
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseProductScreen(
    navController: NavController,
    screenTitle: String,
    initialProductData: Product? = null,
    onSave: (Product) -> Unit,
    onDelete: (() -> Unit)? = null,

    imageData: String, // Chuỗi Base64 hiện tại
    onImageSelected: (String) -> Unit, // Hàm được gọi khi chọn ảnh mới (trả về Base64)
    onImageRemove: () -> Unit // Hàm được gọi khi muốn xóa ảnh
) {
    val appBlueColor = Color(0xFF0088FF)
    val context = LocalContext.current

    // State các trường (CHÚ Ý: ĐÃ XÓA 'var imageData' Ở ĐÂY)
    var tenMatHang by remember { mutableStateOf(initialProductData?.tenMatHang.orEmpty()) }
    var maMatHang by remember { mutableStateOf(initialProductData?.maMatHang.orEmpty()) }
    var soLuong by remember { mutableStateOf(initialProductData?.soLuong?.toString() ?: "0") }
    var giaBan by remember { mutableStateOf(initialProductData?.giaBan?.toString() ?: "0") }
    var giaNhap by remember { mutableStateOf(initialProductData?.giaNhap?.toString() ?: "0") }
    var donViTinh by remember { mutableStateOf(initialProductData?.donViTinh ?: "Kg") }
    var apDungThue by remember { mutableStateOf(initialProductData?.apDungThue ?: true) }
    var ghiChu by remember { mutableStateOf(initialProductData?.ghiChu.orEmpty()) }

    // Dropdown
    val donViOptions = listOf("Cái", "Chai", "Đôi", "Hộp", "Kg", "Lọ", "Thùng")
    var donViExpanded by remember { mutableStateOf(false) }

    // --- LAUNCHER CHỌN ẢNH ---
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            val base64 = uriToBase64(context, uri)
            onImageSelected(base64) // GỌI CALLBACK ĐỂ CẬP NHẬT ẢNH MỚI
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                },
                actions = {
                    // Nút XÓA
                    onDelete?.let {
                        IconButton(onClick = it) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Xóa sản phẩm",
                                tint = Color.White
                            )
                        }
                    }

                    // Nút LƯU
                    TextButton(onClick = {
                        val product = Product(
                            documentId = initialProductData?.documentId ?: "",
                            tenMatHang = tenMatHang,
                            maMatHang = maMatHang,
                            soLuong = soLuong.toDoubleOrNull() ?: 0.0,
                            giaBan = giaBan.toDoubleOrNull() ?: 0.0,
                            giaNhap = giaNhap.toDoubleOrNull() ?: 0.0,
                            donViTinh = donViTinh,
                            apDungThue = apDungThue,
                            ghiChu = ghiChu,
                            imageData = imageData
                        )
                        onSave(product)
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

        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F2F5))
                    .verticalScroll(rememberScrollState())
            ) {
                // --- VÙNG HIỂN THỊ VÀ CHỌN ẢNH (SỬ DỤNG COMPOSABLE PHỤ TRỢ MỚI) ---
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ProductImageUploader(
                        imageData = imageData, // Truyền dữ liệu ảnh hiện tại
                        onImageClick = {
                            // Mở trình chọn ảnh khi click
                            imagePickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        onImageRemove = onImageRemove // Truyền hàm xóa ảnh
                    )
                }

                // ... (Các trường thông tin khác giữ nguyên)
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {
                    // ... (Các InfoTextField giữ nguyên)
                    InfoTextField("Tên mặt hàng", tenMatHang) { tenMatHang = it }
                    Divider()
                    InfoTextField("Mã mặt hàng", maMatHang) { maMatHang = it }
                    Divider()
                    InfoTextField("Số lượng", soLuong) { soLuong = it }
                    Divider()
                    InfoTextField("Giá bán", giaBan) { giaBan = it }
                    Divider()
                    InfoTextField("Giá nhập", giaNhap) { giaNhap = it }
                    Divider()

                    InfoRowWithDropdown(
                        label = "Đơn vị tính",
                        value = donViTinh,
                        options = donViOptions,
                        expanded = donViExpanded,
                        onExpandedChange = { donViExpanded = it },
                        onOptionSelected = { donViTinh = it }
                    )

                    Divider()
                    InfoRowWithSwitch("Áp dụng thuế", apDungThue) { apDungThue = it }
                }

                // Ghi chú (giữ nguyên)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    placeholder = { Text("Ghi chú") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }
    )
}

// --- COMPOSABLE PHỤ TRỢ MỚI: HIỂN THỊ VÀ XỬ LÝ ẢNH ---
@Composable
fun ProductImageUploader(
    imageData: String,
    onImageClick: () -> Unit,
    onImageRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.LightGray.copy(alpha = 0.5f))
            .clickable(onClick = onImageClick), // Click để chọn/thay ảnh
        contentAlignment = Alignment.Center
    ) {
        if (imageData.isEmpty()) {
            // Trường hợp không có ảnh
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Thêm ảnh",
                tint = Color.Gray,
                modifier = Modifier.size(40.dp)
            )
        } else {
            // Trường hợp có ảnh, hiển thị Base64
            // Chú ý: Coil/AsyncImage có thể load Base64 string hoặc ByteArray sau khi decode
            AsyncImage(
                model = Base64.decode(imageData, Base64.DEFAULT),
                contentDescription = "Ảnh sản phẩm",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Nút Xóa ảnh
            IconButton(
                onClick = onImageRemove,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(percent = 50))
                    .size(24.dp)
            ) {
                Text("X", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


// --- CÁC HÀM COMPOSABLE PHỤ TRỢ CŨ (GIỮ NGUYÊN) ---
@Composable
private fun InfoTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

@Composable
private fun InfoRowWithDropdown(
    label: String,
    value: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandedChange(!expanded) }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, modifier = Modifier.weight(1f))
            Text(value, color = Color.Gray)
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun InfoRowWithSwitch(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF0088FF)
            )
        )
    }
}