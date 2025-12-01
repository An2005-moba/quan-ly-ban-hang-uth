package com.nhom10.quanlybanhang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.model.Order
import com.nhom10.quanlybanhang.data.repository.OrderRepository
import com.nhom10.quanlybanhang.data.repository.OrderRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

// State chứa 11 chỉ số
data class ReportUiState(
    val loiNhuan: Double = 0.0,
    val doanhThu: Double = 0.0, // Thực thu
    val soHoaDon: Int = 0,
    val giaTriHoaDon: Double = 0.0, // Tổng tiền hàng chưa giảm giá
    val tienThue: Double = 0.0,
    val giamGia: Double = 0.0,
    val tienBan: Double = 0.0, // Tiền bán sau khi trừ chiết khấu
    val tienVon: Double = 0.0,
    val tienMat: Double = 0.0,
    val nganHang: Double = 0.0,
    val khachNo: Double = 0.0
)

enum class TimeFilter { TODAY, THIS_WEEK, THIS_MONTH, CUSTOM }
enum class StatusFilter { ALL, DELETED }

class ReportViewModel : ViewModel() {
    private val orderRepo: OrderRepository = OrderRepositoryImpl()
    private val auth = FirebaseAuth.getInstance()

    private val _allOrders = MutableStateFlow<List<Order>>(emptyList()) // Danh sách gốc
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    // Filter States
    private val _timeFilter = MutableStateFlow(TimeFilter.TODAY)
    val timeFilter = _timeFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow(StatusFilter.ALL) // Mặc định hiển thị Active (xử lý logic bên dưới)
    val statusFilter = _statusFilter.asStateFlow()

    // Cho Custom Date Range
    private val _customStartDate = MutableStateFlow(startOfDay(System.currentTimeMillis()))
    val customStartDate = _customStartDate.asStateFlow()
    private val _customEndDate = MutableStateFlow(endOfDay(System.currentTimeMillis()))
    val customEndDate = _customEndDate.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            orderRepo.getOrders(userId).onSuccess { list ->
                _allOrders.value = list
                calculateStats() // Tính toán lần đầu
            }
        }
    }

    // --- CÁC HÀM SET FILTER ---
    fun setTimeFilter(filter: TimeFilter) {
        _timeFilter.value = filter
        calculateStats()
    }

    fun setStatusFilter(filter: StatusFilter) {
        _statusFilter.value = filter
        calculateStats()
    }

    fun setCustomDateRange(start: Long, end: Long) {
        _customStartDate.value = start
        _customEndDate.value = end
        if (_timeFilter.value == TimeFilter.CUSTOM) {
            calculateStats()
        }
    }

    // --- LOGIC TÍNH TOÁN 11 CHỈ SỐ ---
    private fun calculateStats() {
        val orders = _allOrders.value
        val timeType = _timeFilter.value
        val statusType = _statusFilter.value

        // 1. Lọc theo thời gian
        val (startTime, endTime) = getTimeRange(timeType)

        // 2. Lọc danh sách
        val filteredOrders = orders.filter { order ->
            val inTimeRange = order.date in startTime..endTime

            val inStatus = if (statusType == StatusFilter.DELETED) {
                order.status == "Đã xóa"
            } else {
                order.status != "Đã xóa" // Mặc định là hiện đơn chưa xóa (Active)
                // Nếu bạn muốn "Tất cả" bao gồm cả đã xóa thì bỏ điều kiện này,
                // nhưng thường báo cáo mặc định chỉ tính đơn thành công.
                // Ở đây tôi làm: Bên phải chọn "Tất cả" (hiện đơn active) hoặc "Đã xóa" (hiện đơn xóa).
            }
            inTimeRange && inStatus
        }

        // 3. Tính toán
        var totalRevenue = 0.0
        var totalProfit = 0.0
        var totalOrders = filteredOrders.size
        var totalInvoiceValue = 0.0 // Tổng tiền hàng (trước giảm giá, thuế)
        var totalTax = 0.0
        var totalDiscount = 0.0
        var totalSales = 0.0 // Tiền bán thực tế (Revenue)
        var totalCost = 0.0
        var totalCash = 0.0
        var totalBank = 0.0
        var totalDebt = 0.0

        filteredOrders.forEach { order ->
            // Tính tiền hàng gốc (Price * Qty)
            val rawTotal = order.items.sumOf { it.giaBan * it.soLuong }
            // Tính tiền vốn (Cost * Qty) - Cần đảm bảo OrderItem có lưu giaVon
            val costTotal = order.items.sumOf { it.giaVon * it.soLuong }

            val discountAmount = rawTotal * (order.chietKhau / 100)

            // Logic 11 chỉ số
            totalInvoiceValue += rawTotal
            totalTax += order.thue
            totalDiscount += discountAmount

            // Tiền bán = Tiền hàng - Giảm giá + Phụ phí
            val finalAmount = order.tongTien
            totalSales += finalAmount
            totalRevenue += finalAmount // Doanh thu = Tổng tiền khách phải trả

            totalCost += costTotal
            totalProfit += ((finalAmount - order.thue) - costTotal)

            // Thanh toán
            if (order.phuongThucTT == "Ngân hàng") {
                totalBank += order.tongTien
            } else {
                totalCash += order.tongTien
            }


        }

        _uiState.value = ReportUiState(
            loiNhuan = totalProfit,
            doanhThu = totalRevenue,
            soHoaDon = totalOrders,
            giaTriHoaDon = totalInvoiceValue,
            tienThue = totalTax,
            giamGia = totalDiscount,
            tienBan = totalSales,
            tienVon = totalCost,
            tienMat = totalCash,
            nganHang = totalBank,
            khachNo = totalDebt
        )
    }

    // Helper lấy khoảng thời gian
    private fun getTimeRange(type: TimeFilter): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = now

        return when (type) {
            TimeFilter.TODAY -> Pair(startOfDay(now), endOfDay(now))
            TimeFilter.THIS_WEEK -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val start = startOfDay(calendar.timeInMillis)
                calendar.add(Calendar.DATE, 6)
                val end = endOfDay(calendar.timeInMillis)
                Pair(start, end)
            }
            TimeFilter.THIS_MONTH -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val start = startOfDay(calendar.timeInMillis)
                calendar.add(Calendar.MONTH, 1)
                calendar.add(Calendar.DATE, -1)
                val end = endOfDay(calendar.timeInMillis)
                Pair(start, end)
            }
            TimeFilter.CUSTOM -> Pair(_customStartDate.value, _customEndDate.value)
        }
    }

    private fun startOfDay(time: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private fun endOfDay(time: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = time
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }
}