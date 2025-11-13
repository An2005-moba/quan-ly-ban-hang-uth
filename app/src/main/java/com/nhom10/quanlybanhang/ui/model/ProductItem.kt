package com.nhom10.quanlybanhang.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductItem(
    var id: String = "",         // ID sản phẩm (Firebase sẽ sinh tự động)
    var name: String = "",       // Tên mặt hàng
    var price: Double = 0.0,     // Giá bán
    var importPrice: Double = 0.0, // Giá nhập
    var quantity: Int = 0,       // Số lượng
    var unit: String = "Cái",    // Đơn vị tính
    var taxApplied: Boolean = false, // Có áp dụng thuế không
    var note: String = ""        // Ghi chú
) : Parcelable
@Parcelize
data class TransactionWithProducts(
    val transactionId: String,
    val date: String,
    val time: String,
    val customerName: String,
    val amount: Double,
    val productList: List<ProductItem>
) : Parcelable