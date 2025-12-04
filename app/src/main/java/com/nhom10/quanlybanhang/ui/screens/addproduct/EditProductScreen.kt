package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf // Thêm
import androidx.compose.runtime.remember // Thêm
import androidx.compose.runtime.setValue // Thêm
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
import android.graphics.BitmapFactory // Dùng để chuyển InputStream thành Bitmap
import android.graphics.Bitmap      // Dùng cho đối tượng Bitmap

@Composable
fun EditProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel
) {
    // 1. Lấy đối tượng Product được truyền từ màn hình trước
    val product = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Product>("product")

    // --- 2. KHAI BÁO STATE CHO DỮ LIỆU ẢNH (Base64) ---
    // Khởi tạo state ảnh với dữ liệu ảnh cũ (nếu có)
    var imageData by remember { mutableStateOf(product?.imageData.orEmpty()) }

    // Chỉ hiển thị màn hình nếu đối tượng Product tồn tại
    if (product != null) {
        BaseProductScreen(
            navController = navController,
            screenTitle = "Chỉnh sửa sản phẩm",
            initialProductData = product,

            // --- 3. TRUYỀN THAM SỐ XỬ LÝ ẢNH MỚI VÀO BASEPRODUCTSCREEN ---
            imageData = imageData, // Truyền Base64 hiện tại

            // Khi người dùng chọn ảnh mới, cập nhật state 'imageData'
            onImageSelected = { newBase64 ->
                imageData = newBase64
            },

            // Khi người dùng nhấn nút xóa ảnh, đặt Base64 về rỗng
            onImageRemove = {
                imageData = ""
            },
            // -----------------------------------------------------------------

            // 4. Logic khi nhấn nút LƯU (Cập nhật sản phẩm)
            onSave = { updatedProduct ->
                // Gán dữ liệu ảnh đã được chỉnh sửa (có thể là ảnh mới, ảnh cũ, hoặc rỗng)
                val finalProduct = updatedProduct.copy(imageData = imageData)

                // Gọi updateProduct để lưu lên Firestore
                productViewModel.updateProduct(
                    finalProduct, // Sử dụng finalProduct đã gán ảnh mới
                    onSuccess = {
                        // Quay lại màn hình trước khi cập nhật thành công
                        navController.popBackStack()
                    },
                    onFailure = { e ->
                        // In lỗi ra console nếu cập nhật thất bại
                        println("Cập nhật thất bại: $e")

                    }
                )
            },

            // 5. Logic khi nhấn nút XÓA (Giữ nguyên)
            onDelete = {
                // Gọi deleteProduct để xóa sản phẩm khỏi Firestore
                productViewModel.deleteProduct(
                    product,
                    onSuccess = {
                        navController.popBackStack()
                    },
                    onFailure = { e: Exception -> // Khai báo kiểu cho 'e'
                        println("Xóa thất bại: $e")
                    }
                )
            }
        )
    }
}