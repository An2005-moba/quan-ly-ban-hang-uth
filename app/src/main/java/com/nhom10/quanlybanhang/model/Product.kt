package com.nhom10.quanlybanhang.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId val documentId: String = "",
    val tenMatHang: String = "",
    val maMatHang: String = "",
    val soLuong: Double = 0.0,
    val giaBan: Double = 0.0,
    val giaNhap: Double = 0.0,
    val donViTinh: String = "Kg",
    val apDungThue: Boolean = true,
    val ghiChu: String = "",
    val imageData: String = ""
)