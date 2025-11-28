package com.nhom10.quanlybanhang.data.model

import com.google.firebase.firestore.DocumentId

data class Customer(
    @DocumentId val id: String = "",
    val tenKhachHang: String = "",
    val soDienThoai: String = "",
    val diaChi: String = "",
    val ghiChu: String = "",
    val avatarUrl: String = "",
    val userId: String = ""
    // Thêm các trường khác nếu cần
)