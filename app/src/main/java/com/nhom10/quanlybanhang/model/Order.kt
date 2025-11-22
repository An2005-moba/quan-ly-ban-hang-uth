package com.nhom10.quanlybanhang.model

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
    val soLuong: Int = 1,
    val donViTinh: String = "Cái"
) : Parcelable

// 2. Thêm @Parcelize và : Parcelable cho Order
@Parcelize
data class Order(
    @DocumentId val id: String = "",
    val customerId: String = "",
    val tenDonHang: String = "",
    val customerName: String = "",
    val items: List<OrderItem> = emptyList(),
    val tongTien: Double = 0.0,
    val chietKhau: Double = 0.0,
    val phuPhi: Double = 0.0,
    val thue: Double = 0.0,
    val ghiChu: String = "",
    val khachTra: Double = 0.0,
    val tienThua: Double = 0.0,
    @ServerTimestamp val ngayTao: Date? = null,
    val status: String = "Hoàn thành",
    val userId: String = "",
    val date: Long = 0L
) : Parcelable