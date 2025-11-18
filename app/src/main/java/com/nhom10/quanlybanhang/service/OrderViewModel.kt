package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.model.Customer
import com.nhom10.quanlybanhang.model.Order
import com.nhom10.quanlybanhang.model.OrderItem
import com.nhom10.quanlybanhang.model.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderViewModel(private val repository: OrderRepository) : ViewModel() {

    // --- 1. CÁC STATE (TRẠNG THÁI) ---

    // Auth để lấy ID người dùng
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId: String? get() = auth.currentUser?.uid

    // Khách hàng
    private val _selectedCustomer = MutableStateFlow<Customer?>(null)
    val selectedCustomer = _selectedCustomer.asStateFlow()

    // Giỏ hàng
    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    // Chiết khấu (%)
    private val _discountPercent = MutableStateFlow(0.0)
    val discountPercent = _discountPercent.asStateFlow()

    // Phụ phí
    private val _surcharge = MutableStateFlow(0.0)
    val surcharge = _surcharge.asStateFlow()

    // Ghi chú
    private val _note = MutableStateFlow("")
    val note = _note.asStateFlow()

    // Thuế
    private val _isTaxEnabled = MutableStateFlow(false)
    val isTaxEnabled = _isTaxEnabled.asStateFlow()

    // Mã đơn hàng
    private val _currentOrderId = MutableStateFlow(generateOrderId())
    val currentOrderId = _currentOrderId.asStateFlow()

    // Tiền khách đưa
    private val _cashGiven = MutableStateFlow(0.0)
    val cashGiven = _cashGiven.asStateFlow()

    // --- 2. LOGIC TÍNH TOÁN TỰ ĐỘNG ---

    // Tổng tiền (Reactive)
    val totalAmount: StateFlow<Double> = combine(
        _cartItems,
        _discountPercent,
        _surcharge,
        _isTaxEnabled
    ) { items, discountVal, surchargeVal, isTax ->
        val itemsTotal = items.sumOf { it.giaBan * it.soLuong }
        val discountAmount = itemsTotal * (discountVal / 100)
        var subTotal = itemsTotal - discountAmount + surchargeVal
        if (subTotal < 0) subTotal = 0.0
        subTotal // (Nếu muốn tính thuế thì nhân thêm ở đây)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // Tiền thừa (Reactive)
    val changeAmount: StateFlow<Double> = combine(
        _cashGiven,
        totalAmount
    ) { cash, total ->
        cash - total
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    // --- 3. KHỞI TẠO & HELPER ---
    init {
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
    }

    private fun generateOrderId(): String {
        val sdf = SimpleDateFormat("yyMMdd.HHmm", Locale.getDefault())
        return "DH.${sdf.format(Date())}"
    }

    // --- 4. CÁC HÀM CẬP NHẬT DỮ LIỆU ---

    fun selectCustomer(customer: Customer) {
        _selectedCustomer.value = customer
    }

    fun updateDiscount(percent: Double) {
        _discountPercent.value = if (percent > 100.0) 100.0 else percent
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

    fun addProductToCart(product: Product) {
        val existingItem = _cartItems.value.find { it.productId == product.documentId }
        if (existingItem != null) {
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
            _cartItems.update { list -> list + newItem }
        }
    }

    fun removeProductFromCart(productId: String) {
        _cartItems.update { list -> list.filterNot { it.productId == productId } }
    }

    fun updateProductQuantity(productId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeProductFromCart(productId)
            return
        }
        _cartItems.update { list ->
            list.map { if (it.productId == productId) it.copy(soLuong = newQuantity) else it }
        }
    }

    // --- 5. LƯU TRỮ VÀ RESET ---

    // Hàm này dùng ở màn hình InvoiceScreen khi bấm "Xong"
    fun saveOrderToFirebase(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
        val userId = currentUserId
        if (userId == null) {
            onFailure(Exception("Chưa đăng nhập"))
            return
        }

        val items = _cartItems.value
        if (items.isEmpty()) return

        val finalOrder = Order(
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
            userId = userId // Quan trọng: Lưu ID người dùng tạo đơn
        )

        viewModelScope.launch {
            val result = repository.saveOrder(userId, finalOrder)
            result.onSuccess {
                clearCart()
                onSuccess()
            }
            result.onFailure { e -> onFailure(e) }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _discountPercent.value = 0.0
        _surcharge.value = 0.0
        _note.value = ""
        _isTaxEnabled.value = false
        _cashGiven.value = 0.0
        // Reset khách hàng
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
        // Tạo mã đơn mới
        _currentOrderId.value = generateOrderId()
    }
}