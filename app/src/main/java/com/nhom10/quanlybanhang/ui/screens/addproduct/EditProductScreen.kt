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
    val product = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Product>("product")

    if (product != null) {
        BaseProductScreen(
            navController = navController,
            screenTitle = "Chỉnh sửa sản phẩm",
            initialProductData = product,
            onSave = { updatedProduct ->
                // Gọi updateProduct để lưu lên Firestore
                productViewModel.updateProduct(
                    updatedProduct,
                    onSuccess = { navController.popBackStack() },
                    onFailure = { e -> println("Cập nhật thất bại: $e") }
                )
            }
        )
    }
}



