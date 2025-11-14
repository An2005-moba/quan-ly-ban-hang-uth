package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.model.Customer
import com.nhom10.quanlybanhang.model.Order
import com.nhom10.quanlybanhang.model.OrderItem
import com.nhom10.quanlybanhang.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    // Khách hàng đang được chọn cho giỏ hàng
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    // Danh sách các mặt hàng trong giỏ
    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // Các giá trị khác của giỏ hàng
    private val _chietKhau = MutableStateFlow(0.0)
    val chietKhau = _chietKhau.asStateFlow()

    init {
        // Mặc định chọn "Khách lẻ"
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
    }

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun addProductToCart(product: Product) {
        val existingItem = _cartItems.value.find { it.productId == product.documentId }

        if (existingItem != null) {
            // Nếu đã có, tăng số lượng
            _cartItems.update { currentList ->
                currentList.map {
                    if (it.productId == existingItem.productId) {
                        it.copy(soLuong = it.soLuong + 1)
                    } else {
                        it
                    }
                }
            }
        } else {
            // Nếu chưa có, thêm mới
            val newItem = OrderItem(
                productId = product.documentId,
                tenMatHang = product.tenMatHang,
                giaBan = product.giaBan,
                soLuong = 1,
                donViTinh = product.donViTinh
            )
            _cartItems.update { currentList -> currentList + newItem }
        }
    }

    fun removeProductFromCart(productId: String) {
        _cartItems.update { currentList ->
            currentList.filterNot { it.productId == productId }
        }
    }

    fun updateProductQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFromCart(productId)
            return
        }
        _cartItems.update { currentList ->
            currentList.map {
                if (it.productId == productId) {
                    it.copy(soLuong = newQuantity)
                } else {
                    it
                }
            }
        }
    }


    fun calculateTotal(): Double {
        return _cartItems.value.sumOf { it.giaBan * it.soLuong }
        // (Thêm logic trừ chiết khấu, cộng thuế, phí... sau)
    }

    // Hàm chốt đơn, lưu vào Firestore
    fun checkout(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val customer = _selectedCustomer.value ?: return // Phải có khách hàng
        val items = _cartItems.value
        if (items.isEmpty()) {
            onFailure(Exception("Giỏ hàng trống"))
            return
        }

        val finalOrder = Order(
            customerId = customer.id,
            customerName = customer.tenKhachHang,
            items = items,
            tongTien = calculateTotal(),
            chietKhau = _chietKhau.value
        )

        viewModelScope.launch {
            val result = repository.saveOrder(finalOrder)
            result.onSuccess {
                clearCart() // Xóa giỏ hàng sau khi thanh toán
                onSuccess()
            }
            result.onFailure { e -> onFailure(e) }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _chietKhau.value = 0.0
        // Reset khách hàng về "Khách lẻ"
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
    }
}