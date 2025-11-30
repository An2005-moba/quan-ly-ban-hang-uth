package com.nhom10.quanlybanhang.ui.screens.payment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nhom10.quanlybanhang.Routes
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BankPaymentScreen(
    navController: NavController,
    orderViewModel: OrderViewModel // Thêm ViewModel để lấy số tiền
) {
    val appBlueColor = Color(0xFF0088FF)
    val scaffoldBgColor = Color(0xFFF0F2F5)

    // Lấy dữ liệu từ ViewModel
    val totalAmount by orderViewModel.totalAmount.collectAsState()
    val orderId by orderViewModel.currentOrderId.collectAsState()

    // --- State lưu thông tin ngân hàng (Thực tế nên lưu vào DataStore/Preferences để dùng lâu dài) ---
    // Danh sách mã ngân hàng xem tại: https://vietqr.io/danh-sach-api/ (VD: MB, VCB, ACB...)
    var bankId by remember { mutableStateOf("VIETINBANK") }
    var accountNo by remember { mutableStateOf("106883202133") } // Nhập số tài khoản của bạn
    var accountName by remember { mutableStateOf("HONG TRUNG KIEN") } // Nhập tên không dấu

    // Chế độ chỉnh sửa thông tin ngân hàng
    var isEditing by remember { mutableStateOf(false) }

    // Link tạo QR động của VietQR (Miễn phí)
    val qrUrl = remember(bankId, accountNo, totalAmount, accountName) {
        "https://img.vietqr.io/image/$bankId-$accountNo-compact2.png?amount=${totalAmount.toLong()}&addInfo=$orderId&accountName=$accountName"
    }

    Scaffold(
        containerColor = scaffoldBgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Chuyển khoản", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    // Nút để sửa thông tin ngân hàng
                    IconButton(onClick = { isEditing = !isEditing }) {
                        Icon(Icons.Default.Edit, contentDescription = "Sửa thông tin")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = appBlueColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomAppBar(containerColor = Color.White, tonalElevation = 8.dp) {
                Button(
                    onClick = {
                        // 1. Cập nhật số tiền khách đã trả (Khách chuyển khoản = Trả đủ)
                        orderViewModel.updateCashGiven(totalAmount)

                        // 2. Chuyển sang màn hình Hóa đơn (InvoiceScreen)
                        // Tại màn hình đó user sẽ bấm "Xong" để lưu về Firestore
                        navController.navigate(Routes.INVOICE)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = appBlueColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Xác nhận đã nhận tiền", fontWeight = FontWeight.Bold)
                }
            }
        },
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
                if (isEditing) {
                    // --- FORM NHẬP THÔNG TIN NGÂN HÀNG ---
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Cài đặt tài khoản nhận tiền", fontWeight = FontWeight.Bold, color = appBlueColor)

                            OutlinedTextField(
                                value = bankId,
                                onValueChange = { bankId = it.uppercase() },
                                label = { Text("Mã Ngân hàng (VD: MB, VCB)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = accountNo,
                                onValueChange = { accountNo = it },
                                label = { Text("Số tài khoản") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = accountName,
                                onValueChange = { accountName = it.uppercase() },
                                label = { Text("Tên chủ tài khoản (Không dấu)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = { isEditing = false },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(containerColor = appBlueColor)
                            ) {
                                Text("Lưu thông tin")
                            }
                        }
                    }
                } else {
                    // --- HIỂN THỊ MÃ QR ---
                    Text(
                        text = "Quét mã để thanh toán",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray
                    )

                    // Card chứa QR Code
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Tải ảnh QR từ VietQR
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(qrUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "VietQR Code",
                                modifier = Modifier
                                    .size(300.dp) // Kích thước QR
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            val formatter = DecimalFormat("#,###")
                            Text(
                                text = "${formatter.format(totalAmount)} đ",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = appBlueColor
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            RowInfo("Ngân hàng", bankId)
                            RowInfo("STK", accountNo)
                            RowInfo("Chủ TK", accountName)
                            RowInfo("Nội dung", orderId)
                        }
                    }

                    Text(
                        text = "Vui lòng kiểm tra ứng dụng ngân hàng để xác nhận tiền đã về tài khoản trước khi bấm Xác nhận.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    )
}

@Composable
private fun RowInfo(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Bold)
    }
}