package com.nhom10.quanlybanhang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.nhom10.quanlybanhang.data.repository.CustomerRepository
import com.nhom10.quanlybanhang.data.model.Customer
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // State chứa danh sách khách hàng
    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    // Biến Job để quản lý việc lắng nghe dữ liệu (để có thể hủy khi đổi user)
    private var dataJob: Job? = null

    // Listener lắng nghe sự thay đổi user (Đăng nhập/Đăng xuất)
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Nếu có user -> Tải dữ liệu của user đó
            Log.d("CustomerViewModel", "User đã đăng nhập: ${user.uid}. Bắt đầu tải data.")
            loadCustomers(user.uid)
        } else {
            // Nếu đăng xuất -> Xóa sạch dữ liệu cũ và hủy lắng nghe
            Log.d("CustomerViewModel", "User đã đăng xuất. Xóa data.")
            _customers.value = emptyList()
            dataJob?.cancel()
        }
    }

    init {
        // Đăng ký listener ngay khi ViewModel được tạo
        auth.addAuthStateListener(authStateListener)
    }

    // Hàm tải dữ liệu (Real-time update)
    private fun loadCustomers(userId: String) {
        // Hủy job cũ nếu đang chạy (để tránh nghe lầm data của user cũ)
        dataJob?.cancel()

        dataJob = viewModelScope.launch {
            repository.getCustomers(userId)
                .catch { e ->
                    Log.e("CustomerViewModel", "Lỗi tải khách hàng: ", e)
                }
                .collect { list ->
                    // Cập nhật danh sách mới lên UI
                    _customers.value = list
                }
        }
    }

    fun addCustomer(
        customer: Customer,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onFailure(Exception("Chưa đăng nhập"))
            return
        }

        viewModelScope.launch {
            try {
                // Gọi repository để thêm vào Firestore
                // Firestore sẽ kích hoạt listener ở hàm loadCustomers -> UI tự cập nhật
                repository.addCustomer(userId, customer)
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Khi ViewModel bị hủy (đóng app hoàn toàn), nhớ gỡ listener để tránh leak memory
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
    fun deleteCustomer(customerId: String) {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.deleteCustomer(userId, customerId)
                // Không cần load lại vì getCustomers là Flow (Realtime)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Lỗi xóa khách: ", e)
            }
        }
    }

    fun deleteAllCustomers() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                repository.deleteAllCustomers(userId)
            } catch (e: Exception) {
                Log.e("CustomerViewModel", "Lỗi xóa tất cả: ", e)
            }
        }
    }
}