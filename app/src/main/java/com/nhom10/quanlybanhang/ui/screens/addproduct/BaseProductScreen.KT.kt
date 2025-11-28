package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
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
import com.nhom10.quanlybanhang.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseProductScreen(
    navController: NavController,
    screenTitle: String,
    initialProductData: Product? = null,
    onSave: (Product) -> Unit
) {
    val appBlueColor = Color(0xFF0088FF)

    // State các trường
    var tenMatHang by remember { mutableStateOf(initialProductData?.tenMatHang.orEmpty()) }
    var maMatHang by remember { mutableStateOf(initialProductData?.maMatHang.orEmpty()) }
    var soLuong by remember { mutableStateOf(initialProductData?.soLuong?.toString() ?: "0") }
    var giaBan by remember { mutableStateOf(initialProductData?.giaBan?.toString() ?: "0") }
    var giaNhap by remember { mutableStateOf(initialProductData?.giaNhap?.toString() ?: "0") }
    var donViTinh by remember { mutableStateOf(initialProductData?.donViTinh ?: "Kg") }
    var apDungThue by remember { mutableStateOf(initialProductData?.apDungThue ?: true) }
    var ghiChu by remember { mutableStateOf(initialProductData?.ghiChu.orEmpty()) }
    var imageData by remember { mutableStateOf(initialProductData?.imageData.orEmpty()) }

    // Dropdown
    val donViOptions = listOf("Cái", "Chai", "Đôi", "Hộp", "Kg", "Lọ", "Thùng")
    var donViExpanded by remember { mutableStateOf(false) }

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
                // Ảnh sơ bộ (placeholder)
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                            .clickable { /* TODO: chọn ảnh */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Thêm ảnh",
                            tint = Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 16.dp)
                ) {
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

                // Ghi chú
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
