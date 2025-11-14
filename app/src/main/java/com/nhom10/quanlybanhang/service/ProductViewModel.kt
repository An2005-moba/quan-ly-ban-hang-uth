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
// THÊM IMPORT NÀY
import com.google.firebase.auth.FirebaseAuth

class ProductViewModel(
    private val repository: ProductRepository // Nhận Repository
) : ViewModel() {

    private val TAG = "ProductViewModel"

    // THÊM 2 DÒNG NÀY
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    // 1. Hàm lắng nghe (đọc) dữ liệu (SỬA LẠI)
    fun loadProducts() {
        // Kiểm tra xem user đã đăng nhập chưa
        val userId = currentUserId
        if (userId == null) {
            _products.value = emptyList() // Nếu chưa, trả về list rỗng
            Log.w(TAG, "User chưa đăng nhập, không thể tải sản phẩm.")
            return
        }

        viewModelScope.launch {
            repository.getProducts(userId) // <-- Truyền userId vào
                .catch { e ->
                    Log.w(TAG, "Lỗi khi tải sản phẩm: ", e)
                }
                .collect { productList ->
                    _products.value = productList
                    Log.d(TAG, "Đã tải ${productList.size} sản phẩm cho user $userId")
                }
        }
    }

    // 2. Hàm thêm (ghi) sản phẩm mới (SỬA LẠI)
    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Kiểm tra xem user đã đăng nhập chưa
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Người dùng chưa đăng nhập."))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addProduct(userId, product) // <-- Truyền userId vào

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.w(TAG, "Lỗi khi thêm sản phẩm", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }
}