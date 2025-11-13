package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.model.ProductItem
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProductScreen(navController: NavController) {
    val product = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<ProductItem>("product")

    if (product != null) {
        BaseProductScreen(
            navController = navController,
            screenTitle = "Chỉnh sửa sản phẩm",
            initialProductData = product,
            onSave = { updatedProduct ->
                println("Cập nhật sản phẩm: $updatedProduct")
                navController.popBackStack()
            }
        )
    }
}

