package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.nhom10.quanlybanhang.model.ProductItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseProductScreen(
    navController: NavController,
    screenTitle: String,
    initialProductData: ProductItem? = null,
    onSave: (ProductItem) -> Unit
) {
    val appBlueColor = Color(0xFF0088FF)

    // State các trường
    var tenMatHang by remember { mutableStateOf(initialProductData?.name.orEmpty()) }
    var maMatHang by remember { mutableStateOf(initialProductData?.id.orEmpty()) }
    var soLuong by remember { mutableStateOf(initialProductData?.quantity?.toString() ?: "0") }
    var giaBan by remember { mutableStateOf(initialProductData?.price?.toString() ?: "0") }
    var giaNhap by remember { mutableStateOf(initialProductData?.importPrice?.toString() ?: "0") }
    var donViTinh by remember { mutableStateOf(initialProductData?.unit.orEmpty().ifEmpty { "Kg" }) }
    var apDungThue by remember { mutableStateOf(initialProductData?.taxApplied ?: true) }
    var ghiChu by remember { mutableStateOf(initialProductData?.note.orEmpty()) }

    // Dropdown state
    val donViOptions = listOf("Cái", "Chai", "Đôi", "Hộp", "Kg", "Lọ", "Thùng")
    var donViExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(screenTitle, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = {
                        val product = ProductItem(
                            id = maMatHang.ifEmpty { "temp_id" },
                            name = tenMatHang,
                            price = giaBan.toDoubleOrNull() ?: 0.0,
                            importPrice = giaNhap.toDoubleOrNull() ?: 0.0,
                            quantity = soLuong.toIntOrNull() ?: 0,
                            unit = donViTinh,
                            taxApplied = apDungThue,
                            note = ghiChu
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
                // Ảnh placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Color.LightGray.copy(alpha = 0.5f))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Image, contentDescription = "Thêm ảnh", tint = Color.Gray, modifier = Modifier.size(40.dp))
                    }
                }

                Column(modifier = Modifier.background(Color.White).padding(horizontal = 16.dp)) {
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
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
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
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF0088FF))
        )
    }
}
