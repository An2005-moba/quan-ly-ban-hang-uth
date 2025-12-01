package com.nhom10.quanlybanhang.data.model

import android.os.Parcelable // Cần import cái này
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize // Cần import cái này
import java.util.Date

// 1. Thêm @Parcelize và : Parcelable cho OrderItem
@Parcelize
data class OrderItem(
    val productId: String = "",
    val tenMatHang: String = "",
    val giaBan: Double = 0.0,
    val giaVon: Double = 0.0, // THÊM: Lưu giá vốn tại thời điểm bán để tính lãi chính xác
    val soLuong: Int = 1,
    val donViTinh: String = "Cái",
    val chietKhau: Double = 0.0,
    val apDungThue: Boolean = false
) : Parcelable

@Parcelize
data class Order(
    @DocumentId val id: String = "",
    val customerId: String = "",
    val tenDonHang: String = "",
    val customerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val tongTien: Double = 0.0,
    val chietKhau: Double = 0.0, // Đơn vị %
    val phuPhi: Double = 0.0,
    val thue: Double = 0.0,
    val ghiChu: String = "",
    val khachTra: Double = 0.0,
    val tienThua: Double = 0.0,
    @ServerTimestamp val ngayTao: Date? = null,
    val status: String = "Hoàn thành", // "Hoàn thành" hoặc "Đã xóa"
    val userId: String = "",
    val date: Long = 0L,
    val phuongThucTT: String = "Tiền mặt" // THÊM: "Tiền mặt" hoặc "Ngân hàng"
) : Parcelable