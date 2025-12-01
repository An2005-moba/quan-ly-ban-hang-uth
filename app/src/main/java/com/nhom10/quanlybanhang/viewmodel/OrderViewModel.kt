package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.model.Customer
import com.nhom10.quanlybanhang.data.model.Order
import com.nhom10.quanlybanhang.data.model.OrderItem
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.data.repository.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel(
    private val repository: OrderRepository,

    private val productRepository: ProductRepository
) : ViewModel() {

    // --- 1. STATE ---
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _discountPercent = MutableStateFlow(0.0)
    val discountPercent = _discountPercent.asStateFlow()

    private val _surcharge = MutableStateFlow(0.0)
    val surcharge = _surcharge.asStateFlow()

    private val _note = MutableStateFlow("")
    val note = _note.asStateFlow()

    private val _isTaxEnabled = MutableStateFlow(false)
    val isTaxEnabled = _isTaxEnabled.asStateFlow()

    private val _currentOrderId = MutableStateFlow(generateOrderId())
    val currentOrderId = _currentOrderId.asStateFlow()

    private val _cashGiven = MutableStateFlow(0.0)
    val cashGiven = _cashGiven.asStateFlow()

    private val _orderHistory = MutableStateFlow<List<Order>>(emptyList())
    val orderHistory = _orderHistory.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _selectedOrderItem = MutableStateFlow<OrderItem?>(null)
    val selectedOrderItem = _selectedOrderItem.asStateFlow()

    // --- 2. LOGIC ---
    val totalAmount: StateFlow<Double> = combine(
        _cartItems, _discountPercent, _surcharge, _isTaxEnabled
    ) { items, discount, surcharge, _ ->

        // Tính tổng tiền các món (Đã trừ chiết khấu từng món)
        val itemsTotal = items.sumOf { item ->
            val priceAfterItemDiscount = item.giaBan * (1 - item.chietKhau / 100)
            priceAfterItemDiscount * item.soLuong
        }

        // Tính tiếp chiết khấu tổng đơn hàng (nếu có)
        val orderDiscountAmount = itemsTotal * (discount / 100)

        maxOf(0.0, itemsTotal - orderDiscountAmount + surcharge)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val changeAmount: StateFlow<Double> = combine(_cashGiven, totalAmount) { cash, total ->
        cash - total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- 3. INIT ---
    init {
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")

        // [FIX QUAN TRỌNG] Tải lịch sử đơn hàng ngay khi ViewModel khởi động.
        // Điều này đảm bảo màn hình History có dữ liệu cũ ngay cả khi chưa mua đơn mới.
        loadOrderHistory()
    }

    private fun generateOrderId(): String {
        val sdf = SimpleDateFormat("yyMMdd.HHmm", Locale.getDefault())
        return "DH.${sdf.format(Date())}"
    }

    // --- 4. ACTIONS ---
    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun updateDiscount(percent: Double) {
        _discountPercent.value = percent.coerceAtMost(100.0)
    }

    fun updateSurcharge(value: Double) {
        _surcharge.value = value
    }

    fun updateNote(value: String) {
        _note.value = value
    }

    fun updateCashGiven(amount: Double) {
        _cashGiven.value = amount
    }

    fun toggleTax(isEnabled: Boolean) {
        _isTaxEnabled.value = isEnabled
    }

    // SỬA: Hàm thêm trả về Boolean và kiểm tra tồn kho
    fun addProductToCart(product: Product): Boolean {
        val existing = _cartItems.value.find { it.productId == product.documentId }
        val currentQtyInCart = existing?.soLuong ?: 0

        // Kiểm tra tồn kho (Product.soLuong là tồn kho)
        if (currentQtyInCart + 1 > product.soLuong) {
            return false // Báo hiệu hết hàng/không đủ hàng
        }

        if (existing != null) {
            _cartItems.update { list ->
                list.map {
                    if (it.productId == product.documentId) it.copy(soLuong = it.soLuong + 1)
                    else it
                }
            }
        } else {
            val newItem = OrderItem(
                productId = product.documentId,
                tenMatHang = product.tenMatHang,
                giaBan = product.giaBan,
                giaVon = product.giaNhap,
                soLuong = 1,
                donViTinh = product.donViTinh
            )
            _cartItems.update { it + newItem }
        }
        return true // Thêm thành công
    }

    // THÊM: Hàm giảm số lượng
    fun decreaseProductFromCart(product: Product) {
        val existing = _cartItems.value.find { it.productId == product.documentId } ?: return

        if (existing.soLuong > 1) {
            // Nếu > 1 thì giảm 1
            _cartItems.update { list ->
                list.map {
                    if (it.productId == product.documentId) it.copy(soLuong = it.soLuong - 1)
                    else it
                }
            }
        } else {
            // Nếu = 1 thì xóa khỏi giỏ
            removeProductFromCart(product.documentId)
        }
    }

    fun removeProductFromCart(productId: String) {
        _cartItems.update { it.filterNot { item -> item.productId == productId } }
    }

    fun updateProductQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFromCart(productId); return
        }
        _cartItems.update { it.map { if (it.productId == productId) it.copy(soLuong = newQuantity) else it } }
    }

    // ĐÃ THÊM: Xử lý xóa đơn hàng
    fun deleteOrder(
        orderId: String,
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) {
        val userId = currentUserId ?: return onFailure(Exception("Chưa đăng nhập"))

        viewModelScope.launch {
            val result = repository.deleteOrder(userId, orderId)
            result.onSuccess {
                // Cập nhật lại danh sách lịch sử đơn hàng (Local Update)
                _orderHistory.update { currentList ->
                    currentList.filter { it.id != orderId }
                }
                onSuccess()
            }
            result.onFailure { onFailure(it) }
        }
    }

    fun selectOrderItem(item: OrderItem) {
        _selectedOrderItem.value = item
    }

    fun updateCartItem(updatedItem: OrderItem) {
        _cartItems.update { list ->
            list.map {
                if (it.productId == updatedItem.productId) updatedItem else it
            }
        }
    }

    // --- 5. SAVE & RESET ---
    fun saveOrderToFirebase(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val userId = currentUserId ?: return onFailure(Exception("Chưa đăng nhập"))
        val items = _cartItems.value
        if (items.isEmpty()) return

        val order = Order(
            id = _currentOrderId.value,
            tenDonHang = _currentOrderId.value,
            customerId = _selectedCustomer.value?.id ?: "",
            customerName = _selectedCustomer.value?.tenKhachHang ?: "",
            items = items,
            tongTien = totalAmount.value,
            chietKhau = _discountPercent.value,
            phuPhi = _surcharge.value,
            ghiChu = _note.value,
            khachTra = _cashGiven.value,
            tienThua = changeAmount.value,
            userId = userId,
            date = System.currentTimeMillis()
        )

        viewModelScope.launch {
            // 2. BẬT LOADING
            _isLoading.value = true

            // Gọi repository (Giữ nguyên logic cũ)
            val orderResult = repository.saveOrder(userId, order)

            orderResult.onSuccess {
                try {
                    productRepository.deductStock(userId, items)

                    _orderHistory.update { currentList ->
                        (listOf(order) + currentList).sortedByDescending { it.date }
                    }
                    clearCart()

                    // 3. TẮT LOADING TRƯỚC KHI GỌI SUCCESS
                    _isLoading.value = false
                    onSuccess()
                } catch (e: Exception) {
                    _isLoading.value = false // Tắt loading nếu lỗi
                    onFailure(Exception("Lưu đơn thành công nhưng lỗi trừ kho: ${e.message}"))
                }
            }
            orderResult.onFailure {
                _isLoading.value = false // Tắt loading nếu lỗi
                onFailure(it)
            }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discountPercent.value = 0.0
        _surcharge.value = 0.0
        _note.value = ""
        _isTaxEnabled.value = false
        _cashGiven.value = 0.0
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
        _currentOrderId.value = generateOrderId()
    }

    fun loadOrderHistory() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.getOrders(userId).onSuccess {
                _orderHistory.value = it.sortedByDescending { order -> order.date }
            }
        }
    }

}