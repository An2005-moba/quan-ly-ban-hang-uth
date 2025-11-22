package com.nhom10.quanlybanhang.service

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// Data class chứa thông tin hiển thị (Mặc định là chưa đăng nhập)
data class AccountUiState(
    val userName: String = "Chưa đăng nhập",
    val userId: String = "",
    val photoUrl: String? = null
)

class AccountViewModel : ViewModel() {

    // Lấy trực tiếp FirebaseAuth để gắn "camera giám sát"
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(AccountUiState())
    val uiState: StateFlow<AccountUiState> = _uiState

    // --- 1. TẠO BỘ LẮNG NGHE (LISTENER) ---
    // Mỗi khi người dùng Đăng nhập hoặc Đăng xuất, hàm này sẽ tự chạy
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        updateUi(firebaseAuth.currentUser)
    }

    init {
        // --- 2. BẮT ĐẦU LẮNG NGHE NGAY KHI KHỞI TẠO ---
        auth.addAuthStateListener(authStateListener)

        // Cập nhật trạng thái hiện tại ngay lập tức
        updateUi(auth.currentUser)
    }

    // Hàm xử lý logic cập nhật UI
    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            // --- TRƯỜNG HỢP ĐÃ ĐĂNG NHẬP ---

            // Lấy tên (Ưu tiên tên từ Google/Auth, nếu không có thì để mặc định)
            val name = if (!user.displayName.isNullOrBlank()) {
                user.displayName
            } else {
                "Người dùng" // Hoặc tên bạn lấy từ Firestore nếu muốn phức tạp hơn
            }

            // Lấy ID (Cắt ngắn 6 ký tự đầu cho đẹp)
            val id = "ID: ${user.uid.take(6)}..."

            // Lấy ảnh (Google có ảnh, Đăng ký tay thì null)
            val photo = user.photoUrl?.toString()

            _uiState.value = AccountUiState(
                userName = name!!,
                userId = id,
                photoUrl = photo
            )
        } else {
            // --- TRƯỜNG HỢP CHƯA ĐĂNG NHẬP / ĐÃ ĐĂNG XUẤT ---
            // Reset về trạng thái rỗng
            _uiState.value = AccountUiState()
        }
    }

    // --- 3. HỦY LẮNG NGHE KHI THOÁT APP ĐỂ TRÁNH LỖI ---
    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}