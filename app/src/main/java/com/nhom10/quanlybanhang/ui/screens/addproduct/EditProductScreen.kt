package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel

@Composable
fun EditProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel
) {
    // Lấy đối tượng Product được truyền từ màn hình trước
    val product = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Product>("product")

    // Chỉ hiển thị màn hình nếu đối tượng Product tồn tại
    if (product != null) {
        BaseProductScreen(
            navController = navController,
            screenTitle = "Chỉnh sửa sản phẩm",
            initialProductData = product,

            // Logic khi nhấn nút LƯU (Cập nhật sản phẩm)
            onSave = { updatedProduct ->
                // Gọi updateProduct để lưu lên Firestore
                productViewModel.updateProduct(
                    updatedProduct,
                    onSuccess = {
                        // Quay lại màn hình trước khi cập nhật thành công
                        navController.popBackStack()
                    },
                    onFailure = { e ->
                        // In lỗi ra console nếu cập nhật thất bại
                        println("Cập nhật thất bại: $e")
                        // TODO: Hiển thị thông báo lỗi (ví dụ: Toast/Snackbar) cho người dùng
                    }
                )
            },

            // Logic khi nhấn nút XÓA (Nút mới được thêm vào)
            onDelete = {
                // Gọi deleteProduct để xóa sản phẩm khỏi Firestore
                productViewModel.deleteProduct(
                    product,
                    onSuccess = {
                        navController.popBackStack()
                    },
                    onFailure = { e: Exception -> // <--- KHAI BÁO KIỂU CHO 'e' Ở ĐÂY
                        println("Xóa thất bại: $e")
                        // TODO: Hiển thị thông báo lỗi cho người dùng
                    }
                )
            }
        )
    }
}