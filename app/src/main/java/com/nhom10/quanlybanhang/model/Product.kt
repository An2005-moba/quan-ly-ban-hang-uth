package com.nhom10.quanlybanhang.model
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.firestore.DocumentId
@Parcelize
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
) : Parcelable