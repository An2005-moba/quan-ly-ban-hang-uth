package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.nhom10.quanlybanhang.model.ProductItem

@Composable
fun AddProductScreen(navController: NavController) {
    BaseProductScreen(
        navController = navController,
        screenTitle = "Thêm mặt hàng",
        initialProductData = null,
        onSave = { product ->
            println("AddProductScreen: $product")
            navController.popBackStack()
        }
    )
}
