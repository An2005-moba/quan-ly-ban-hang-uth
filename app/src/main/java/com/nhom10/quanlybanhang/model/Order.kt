package com.nhom10.quanlybanhang.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// Mục hàng trong giỏ
data class OrderItem(
    val productId: String = "", // Lấy từ Product.documentId
    val tenMatHang: String = "",
    val giaBan: Double = 0.0,
    val soLuong: Int = 1,
    val donViTinh: String = "Cái"
)

// Toàn bộ đơn hàng sẽ lưu vào Firestore
data class Order(
    @DocumentId val id: String = "",
    val customerId: String = "", // ID từ Customer
    val tenDonHang: String = "",
    val customerName: String = "", // Tên khách hàng
    val items: List<OrderItem> = emptyList(), // Danh sách các mục hàng
    val tongTien: Double = 0.0,
    val chietKhau: Double = 0.0,
    val phuPhi: Double = 0.0,
    val thue: Double = 0.0,
    // --- THÊM CÁC TRƯỜNG NÀY ---
    val ghiChu: String = "",      // Lưu ghi chú
    val khachTra: Double = 0.0,   // Lưu tiền khách đưa
    val tienThua: Double = 0.0,
    @ServerTimestamp val ngayTao: Date? = null,
    val status: String = "Hoàn thành", // (ví dụ: Hoàn thành, Đã hủy)
    val userId: String = ""
)