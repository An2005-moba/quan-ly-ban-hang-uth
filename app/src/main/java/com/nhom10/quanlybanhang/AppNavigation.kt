package com.nhom10.quanlybanhang // Đảm bảo đây là tên gói gốc của bạn

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nhom10.quanlybanhang.ui.screens.home.HomeScreen
import com.nhom10.quanlybanhang.ui.screens.settings.SettingsScreen
import com.nhom10.quanlybanhang.ui.screens.editprofile.EditProfileScreen
import com.nhom10.quanlybanhang.ui.screens.password.PasswordScreen
import com.nhom10.quanlybanhang.ui.screens.password.ChangePasswordScreen
import com.nhom10.quanlybanhang.ui.screens.language.LanguageScreen
import com.nhom10.quanlybanhang.ui.screens.productsetup.ProductSetupScreen
import com.nhom10.quanlybanhang.ui.screens.addproduct.AddProductScreen
import com.nhom10.quanlybanhang.ui.screens.addproduct.EditProductScreen
import com.nhom10.quanlybanhang.ui.screens.cart.CartScreen
import com.nhom10.quanlybanhang.ui.screens.cart.EditOrderItemScreen
import com.nhom10.quanlybanhang.ui.screens.customer.SelectCustomerScreen
import com.nhom10.quanlybanhang.ui.screens.customer.AddCustomerScreen
import com.nhom10.quanlybanhang.ui.screens.customer.AddOrderItemScreen
import com.nhom10.quanlybanhang.ui.screens.payment.InvoiceScreen
import com.nhom10.quanlybanhang.ui.screens.payment.PaymentScreen
import com.nhom10.quanlybanhang.ui.screens.payment.BankPaymentScreen
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.data.ProductRepositoryImpl
import com.nhom10.quanlybanhang.service.ProductViewModel
import com.nhom10.quanlybanhang.service.ProductViewModelFactory
import com.nhom10.quanlybanhang.ui.screens.history.HistoryScreen
import com.nhom10.quanlybanhang.ui.screens.history.BillDetailScreen
object Routes {
    const val HOME = "home_screen"
    const val SETTINGS = "settings_screen"
    const val EDIT_PROFILE = "edit_profile_screen"
    const val PASSWORD = "password_screen"
    const val CHANGE_PASSWORD = "change_password_screen"
    const val LANGUAGE = "language_screen"
    const val PRODUCT_SETUP = "product_setup_screen"
    const val ADD_PRODUCT = "add_product_screen"
    const val EDIT_PRODUCT = "edit_product_screen"
    const val CART = "cart_screen"
    const val SELECT_CUSTOMER = "select_customer_screen"
    const val ADD_CUSTOMER = "add_customer_screen"
    const val ADD_ORDER_ITEM = "add_order_item_screen"
    const val EDIT_ORDER_ITEM = "edit_order_item_screen"
    const val PAYMENT = "payment_screen"
    const val BANK_PAYMENT = "bank_payment_screen"
    const val HISTORY = "history_screen"
    const val BILL = "bill_screen"

    // === ROUTE INVOICE ===
    const val INVOICE = "invoice_screen/{khachTra}/{tienThua}"
    fun invoiceRoute(khachTra: String, tienThua: String) = "invoice_screen/$khachTra/$tienThua"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    // 1. Tạo Repository (Lớp Data) 1 lần duy nhất
    val productRepository = ProductRepositoryImpl()

    // 2. Tạo Factory cho ViewModel
    val productViewModelFactory = ProductViewModelFactory(productRepository)

    // 3. Tạo ViewModel (Lớp Service/Logic) 1 lần duy nhất
    val productViewModel: ProductViewModel = viewModel(
        factory = productViewModelFactory
    )
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(onBackClicked = { navController.popBackStack() })
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
            ProductSetupScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Routes.EDIT_PRODUCT) {
            EditProductScreen(navController = navController)
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
        composable(Routes.EDIT_ORDER_ITEM) {
            EditOrderItemScreen(navController = navController)
        }
        composable(Routes.PAYMENT) {
            PaymentScreen(navController = navController)
        }
        composable(
            route = Routes.INVOICE,
            arguments = listOf(
                navArgument("khachTra") { type = NavType.StringType },
                navArgument("tienThua") { type = NavType.StringType }
            )
        ) { navBackStackEntry ->
            val khachTra = navBackStackEntry.arguments?.getString("khachTra") ?: "0"
            val tienThua = navBackStackEntry.arguments?.getString("tienThua") ?: "0"

            InvoiceScreen(
                navController = navController,
                khachTra = khachTra,
                tienThua = tienThua
            )
        }
        composable(Routes.BANK_PAYMENT) {
            BankPaymentScreen(navController = navController)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(navController = navController)
        }
        composable(Routes.BILL) {
            BillDetailScreen(navController = navController)
        }
    }
}
