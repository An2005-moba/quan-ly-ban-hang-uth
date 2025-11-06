package com.nhom10.quanlybanhang // Đảm bảo đây là tên gói gốc của bạn

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.ui.screens.home.HomeScreen
import com.nhom10.quanlybanhang.ui.screens.settings.SettingsScreen
import com.nhom10.quanlybanhang.ui.screens.editprofile.EditProfileScreen
import com.nhom10.quanlybanhang.ui.screens.password.PasswordScreen
import com.nhom10.quanlybanhang.ui.screens.password.ChangePasswordScreen
import com.nhom10.quanlybanhang.ui.screens.language.LanguageScreen
import com.nhom10.quanlybanhang.ui.screens.productsetup.ProductSetupScreen
import com.nhom10.quanlybanhang.ui.screens.addproduct.AddProductScreen
import com.nhom10.quanlybanhang.ui.screens.cart.CartScreen
import com.nhom10.quanlybanhang.ui.screens.customer.SelectCustomerScreen
import com.nhom10.quanlybanhang.ui.screens.customer.AddCustomerScreen
import com.nhom10.quanlybanhang.ui.screens.customer.AddOrderItemScreen
object Routes {
    const val HOME = "home_screen"
    const val SETTINGS = "settings_screen"
    const val EDIT_PROFILE = "edit_profile_screen"
    const val PASSWORD = "password_screen"
    const val CHANGE_PASSWORD = "change_password_screen"
    const val LANGUAGE = "language_screen"
    const val PRODUCT_SETUP = "product_setup_screen"
    const val ADD_PRODUCT = "add_product_screen"
    const val CART = "cart_screen"
    const val SELECT_CUSTOMER = "select_customer_screen"
    const val ADD_CUSTOMER = "add_customer_screen"
    const val ADD_ORDER_ITEM = "add_order_item_screen"

}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController)
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(
                onBackClicked = { navController.popBackStack() }
            )
        }

        composable(Routes.PASSWORD) {
            PasswordScreen(navController = navController)
        }

        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }

        composable(Routes.LANGUAGE) {
            LanguageScreen(navController = navController)
        }

        composable(Routes.PRODUCT_SETUP) {
            ProductSetupScreen(navController = navController)
        }

        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController = navController)
        }
        composable(Routes.CART) {
            CartScreen(navController = navController)
        }
        composable(Routes.SELECT_CUSTOMER) {
            SelectCustomerScreen(navController = navController)
        }
        composable(Routes.ADD_CUSTOMER) {
            AddCustomerScreen(navController = navController)
        }
        composable(Routes.ADD_ORDER_ITEM) {
            AddOrderItemScreen(navController = navController)
        }


    }
}