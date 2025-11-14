package com.nhom10.quanlybanhang.service


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.ProductRepository // Import
import com.nhom10.quanlybanhang.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.Uri
import java.util.UUID // <-- THÊM IMPORT NÀY

class ProductViewModel(
    private val repository: ProductRepository // Nhận Repository
) : ViewModel() {

    private val TAG = "ProductViewModel"

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // 1. Hàm lắng nghe (đọc) dữ liệu (Giữ nguyên)
    fun loadProducts() {
        viewModelScope.launch {
            repository.getProducts() // Gọi từ repository
                .catch { e ->
                    // Xử lý lỗi khi lắng nghe
                    Log.w(TAG, "Lỗi khi tải sản phẩm: ", e)
                }
                .collect { productList ->
                    _products.value = productList
                    Log.d(TAG, "Đã tải ${productList.size} sản phẩm")
                }
        }
    }

    // 2. Hàm thêm (ghi) sản phẩm mới (THAY ĐỔI HOÀN TOÀN)
    fun addProduct(
        product: Product, // Chỉ nhận Product (đã có imageData)
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Chỉ cần gọi addProduct
                repository.addProduct(product)

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.w(TAG, "Lỗi khi thêm sản phẩm", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }
}