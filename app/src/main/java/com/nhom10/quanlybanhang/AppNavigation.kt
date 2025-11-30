package com.nhom10.quanlybanhang

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nhom10.quanlybanhang.ui.screens.font.FontSizeScreen
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel

import androidx.navigation.navArgument
import com.nhom10.quanlybanhang.ui.screens.auth.LoginScreen
import com.nhom10.quanlybanhang.ui.screens.auth.RegisterScreen
import com.nhom10.quanlybanhang.ui.screens.auth.ForgotPasswordScreen
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nhom10.quanlybanhang.data.repository.ProductRepositoryImpl
import com.nhom10.quanlybanhang.data.repository.CustomerRepositoryImpl
import com.nhom10.quanlybanhang.data.repository.OrderRepositoryImpl
import com.nhom10.quanlybanhang.viewmodel.CustomerViewModel
import com.nhom10.quanlybanhang.viewmodel.CustomerViewModelFactory
import com.nhom10.quanlybanhang.viewmodel.OrderViewModel
import com.nhom10.quanlybanhang.viewmodel.OrderViewModelFactory
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel
import com.nhom10.quanlybanhang.viewmodel.ProductViewModelFactory
import com.nhom10.quanlybanhang.ui.screens.history.HistoryScreen
import com.nhom10.quanlybanhang.ui.screens.history.BillDetailScreen
import com.nhom10.quanlybanhang.ui.screens.theme.ThemeScreen

object Routes {
    const val LOGIN = "login_screen"
    const val REGISTER = "register_screen"
    const val FORGOT_PASSWORD = "forgot_password_screen"
    const val HOME = "home_screen"
    const val SETTINGS = "settings_screen"
    const val EDIT_PROFILE = "edit_profile_screen"
    const val PASSWORD = "password_screen"
    const val CHANGE_PASSWORD = "change_password_screen"
    const val THEME = "theme_screen"
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
    const val INVOICE = "invoice_screen" // Sửa lỗi typo dư dấu }
    fun invoiceRoute(khachTra: String, tienThua: String) = "invoice_screen/$khachTra/$tienThua"
    const val FONT_SIZE = "font_size_screen"
}

@Composable
fun AppNavigation(
    startDestination: String,
    fontSizeViewModel: FontSizeViewModel// <-- Thêm startDestination với giá trị mặc định là LOGIN
) {
    val navController = rememberNavController()

    // --- Giữ nguyên ViewModel ---
    val productRepository = ProductRepositoryImpl()
    val productViewModelFactory = ProductViewModelFactory(productRepository)
    val productViewModel: ProductViewModel = viewModel(
        factory = productViewModelFactory
    )

    val customerRepository = CustomerRepositoryImpl()
    val customerViewModelFactory = CustomerViewModelFactory(customerRepository)
    val customerViewModel: CustomerViewModel = viewModel(
        factory = customerViewModelFactory
    )

    val orderRepository = OrderRepositoryImpl()
    val orderViewModelFactory = OrderViewModelFactory(orderRepository)
    val orderViewModel: OrderViewModel = viewModel(
        factory = orderViewModelFactory
    )
    // ----------------------------------------------------

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Routes.HOME) {
            HomeScreen(
                navController = navController,
                productViewModel = productViewModel,
                orderViewModel = orderViewModel
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(navController = navController,
            fontSizeViewModel = fontSizeViewModel)
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController = navController)
        }
        composable(Routes.PASSWORD) {
            PasswordScreen(navController = navController)
        }
        composable(Routes.CHANGE_PASSWORD) {
            ChangePasswordScreen(navController = navController)
        }
        composable(Routes.THEME) {
            ThemeScreen(navController = navController)
        }
        composable(Routes.PRODUCT_SETUP) {
            ProductSetupScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Routes.ADD_PRODUCT) {
            AddProductScreen(navController = navController, productViewModel = productViewModel)
        }
        composable(Routes.EDIT_PRODUCT) {
            EditProductScreen(navController = navController,
                productViewModel = productViewModel)
        }

        composable(Routes.CART) {
            CartScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable(Routes.SELECT_CUSTOMER) {
            SelectCustomerScreen(
                navController = navController,
                customerViewModel = customerViewModel,
                orderViewModel = orderViewModel
            )
        }

        composable(Routes.ADD_CUSTOMER) {
            AddCustomerScreen(
                navController = navController,
                customerViewModel = customerViewModel
            )
        }

        composable(Routes.ADD_ORDER_ITEM) {
            AddOrderItemScreen(
                navController = navController,
                productViewModel = productViewModel,
                orderViewModel = orderViewModel
            )
        }

        composable(Routes.EDIT_ORDER_ITEM) {
            EditOrderItemScreen(navController = navController)
        }
        composable(Routes.PAYMENT) {
            PaymentScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable(Routes.INVOICE) {
            InvoiceScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable(Routes.BANK_PAYMENT) {
            BankPaymentScreen(navController = navController)
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                navController = navController,
                orderViewModel = orderViewModel
            )
        }
        composable(Routes.BILL) {
            BillDetailScreen(navController = navController)

        }
        composable(Routes.FONT_SIZE) {
            FontSizeScreen(
                navController = navController,
                fontSizeViewModel = fontSizeViewModel
            )
        }

    }
}
