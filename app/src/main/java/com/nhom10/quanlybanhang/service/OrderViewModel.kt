package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

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

    // --- 2. LOGIC ---
    val totalAmount: StateFlow<Double> = combine(
        _cartItems, _discountPercent, _surcharge, _isTaxEnabled
    ) { items, discount, surcharge, _ ->
        val itemsTotal = items.sumOf { it.giaBan * it.soLuong }
        val discountAmount = itemsTotal * (discount / 100)
        maxOf(0.0, itemsTotal - discountAmount + surcharge)
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
    fun selectCustomer(customer: Customer) { _selectedCustomer.value = customer }
    fun updateDiscount(percent: Double) { _discountPercent.value = percent.coerceAtMost(100.0) }
    fun updateSurcharge(value: Double) { _surcharge.value = value }
    fun updateNote(value: String) { _note.value = value }
    fun updateCashGiven(amount: Double) { _cashGiven.value = amount }
    fun toggleTax(isEnabled: Boolean) { _isTaxEnabled.value = isEnabled }

    fun addProductToCart(product: Product) {
        val existing = _cartItems.value.find { it.productId == product.documentId }
        if (existing != null) {
            _cartItems.update { list ->
                list.map { if (it.productId == product.documentId) it.copy(soLuong = it.soLuong + 1) else it }
            }
        } else {
            val newItem = OrderItem(
                productId = product.documentId,
                tenMatHang = product.tenMatHang,
                giaBan = product.giaBan,
                soLuong = 1,
                donViTinh = product.donViTinh
            )
            _cartItems.update { it + newItem }
        }
    }

    fun removeProductFromCart(productId: String) {
        _cartItems.update { it.filterNot { item -> item.productId == productId } }
    }

    fun updateProductQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) { removeProductFromCart(productId); return }
        _cartItems.update { it.map { if (it.productId == productId) it.copy(soLuong = newQuantity) else it } }
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
            val result = repository.saveOrder(userId, order)
            result.onSuccess {
                // Cập nhật ngay danh sách orderHistory để hiển thị HistoryScreen (Optimistic Update)
                // Vì chúng ta cập nhật ở đây, đơn hàng mới sẽ hiện ngay lập tức.
                _orderHistory.update { currentList ->
                    (listOf(order) + currentList).sortedByDescending { it.date }
                }

                clearCart()
                onSuccess()
            }
            result.onFailure { onFailure(it) }
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