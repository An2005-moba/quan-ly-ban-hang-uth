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
    private val _paymentMethod = MutableStateFlow("Tiền mặt")
    val paymentMethod = _paymentMethod.asStateFlow()

    // --- 2. LOGIC ---
    // --- SỬA ĐỔI: LOGIC TÍNH THUẾ (Tính trên giá thực thu sau khi giảm giá món) ---
    val taxAmount: StateFlow<Double> = _cartItems.map { items ->
        items.sumOf { item ->
            if (item.apDungThue) {
                // Giá trị của dòng = (Giá x Số lượng) - Chiết khấu món
                val itemTotal = item.giaBan * item.soLuong
                val itemDiscount = itemTotal * (item.chietKhau / 100)
                val finalItemTotal = itemTotal - itemDiscount
                finalItemTotal * 0.10 // 10% thuế trên giá thực bán
            } else {
                0.0
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // --- SỬA ĐỔI: LOGIC TỔNG TIỀN (Tính chiết khấu món trước, rồi mới tính tổng) ---
    val totalAmount: StateFlow<Double> = combine(
        _cartItems, _discountPercent, _surcharge, taxAmount
    ) { items, discount, surcharge, tax ->
        // 1. Tính tổng tiền hàng (Sau khi đã trừ chiết khấu của TỪNG MÓN)
        val itemsTotalAfterItemDiscount = items.sumOf { item ->
            val rawTotal = item.giaBan * item.soLuong
            val itemDiscountAmount = rawTotal * (item.chietKhau / 100)
            rawTotal - itemDiscountAmount
        }

        // 2. Tính giảm giá tổng đơn hàng (trên tổng tiền đã trừ chiết khấu món)
        // Hoặc tùy logic shop, thường là giảm trên tổng tiền hàng thực tế
        val orderDiscountAmount = itemsTotalAfterItemDiscount * (discount / 100)
        // 3. Công thức cuối cùng
        maxOf(0.0, itemsTotalAfterItemDiscount - orderDiscountAmount + tax + surcharge)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val changeAmount: StateFlow<Double> = combine(_cashGiven, totalAmount) { cash, total ->
        cash - total
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        _selectedCustomer.value = Customer(id = "khach_le", tenKhachHang = "Khách lẻ")
        loadOrderHistory()
    }

    private fun generateOrderId(): String {
        val sdf = SimpleDateFormat("yyMMdd.HHmm", Locale.getDefault())
        return "DH.${sdf.format(Date())}"
    }

    // --- 4. ACTIONS ---
    fun setPaymentMethod(method: String) {
        _paymentMethod.value = method
    }

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

        if (currentQtyInCart + 1 > product.soLuong) return false

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
                donViTinh = product.donViTinh,
                // Lấy trạng thái thuế từ sản phẩm
                apDungThue = product.apDungThue
            )
            _cartItems.update { it + newItem }
        }
        return true
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
            // Lấy giá trị thuế đã tính toán
            thue = taxAmount.value,
            userId = userId,
            date = System.currentTimeMillis(),
            phuongThucTT = _paymentMethod.value
        )

        viewModelScope.launch {
            _isLoading.value = true
            val orderResult = repository.saveOrder(userId, order)
            orderResult.onSuccess {
                try {
                    productRepository.deductStock(userId, items)
                    _orderHistory.update { (listOf(order) + it).sortedByDescending { o -> o.date } }
                    clearCart()
                    _isLoading.value = false
                    onSuccess()
                } catch (e: Exception) {
                    _isLoading.value = false
                    onFailure(Exception("Lưu thành công nhưng lỗi trừ kho: ${e.message}"))
                }
            }
            orderResult.onFailure {
                _isLoading.value = false
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
        _paymentMethod.value = "Tiền mặt"
    }

    fun loadOrderHistory() {
        val userId = currentUserId ?: return
        viewModelScope.launch {
            repository.getOrders(userId).onSuccess { allOrders ->
                // SỬA: Chỉ lấy những đơn CHƯA XÓA để hiện lên màn hình Lịch sử
                _orderHistory.value = allOrders
                    .filter { it.status != "Đã xóa" } // <-- Lọc tại đây
                    .sortedByDescending { order -> order.date }
            }
        }
    }

}