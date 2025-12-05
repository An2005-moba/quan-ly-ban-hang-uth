package com.nhom10.quanlybanhang.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.ProductRepository
import com.nhom10.quanlybanhang.data.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.update

import com.google.firebase.auth.FirebaseAuth

class ProductViewModel(
    private val repository: ProductRepository
) : ViewModel() {

    private val TAG = "ProductViewModel"

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    // Lấy ID người dùng hiện tại
    private val currentUserId: String? get() = auth.currentUser?.uid

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    //  Hàm lắng nghe (đọc) dữ liệu
    fun loadProducts() {
        val userId = currentUserId
        if (userId == null) {
            _products.value = emptyList()
            Log.w(TAG, "User chưa đăng nhập, không thể tải sản phẩm.")
            return
        }

        viewModelScope.launch {
            repository.getProducts(userId)
                .catch { e ->
                    Log.w(TAG, "Lỗi khi tải sản phẩm: ", e)
                }
                .collect { productList ->
                    _products.value = productList
                    Log.d(TAG, "Đã tải ${productList.size} sản phẩm cho user $userId")
                }
        }
    }

    //  Hàm thêm (ghi) sản phẩm mới
    fun addProduct(
        product: Product,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Người dùng chưa đăng nhập."))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.addProduct(userId, product)

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.w(TAG, "Lỗi khi thêm sản phẩm", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }

        }
    }

    // 3. Hàm cập nhật sản phẩm
    fun updateProduct(
        updatedProduct: Product,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Người dùng chưa đăng nhập."))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Cập nhật lên repository / Firebase
                repository.updateProduct(userId, updatedProduct)

                // Cập nhật local StateFlow để UI tự động refresh
                _products.update { currentList ->
                    currentList.map { if (it.documentId == updatedProduct.documentId) updatedProduct else it }
                }

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.w(TAG, "Lỗi khi cập nhật sản phẩm", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

    //  HÀM XÓA SẢN PHẨM (MỚI THÊM)
    fun deleteProduct(
        product: Product,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Người dùng chưa đăng nhập."))
            return
        }

        // Kiểm tra xem sản phẩm có documentId hợp lệ không
        if (product.documentId.isEmpty()) {
            onFailure(Exception("Thiếu ID sản phẩm để xóa."))
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Xóa trên repository / Firebase
                repository.deleteProduct(userId, product.documentId)

                // 2. Cập nhật local StateFlow để UI tự động refresh
                _products.update { currentList ->
                    currentList.filter { it.documentId != product.documentId }
                }

                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                Log.w(TAG, "Lỗi khi xóa sản phẩm", e)
                withContext(Dispatchers.Main) { onFailure(e) }
            }
        }
    }

}