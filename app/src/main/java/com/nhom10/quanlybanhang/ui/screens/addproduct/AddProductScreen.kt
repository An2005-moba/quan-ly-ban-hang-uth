package com.nhom10.quanlybanhang.ui.screens.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.nhom10.quanlybanhang.data.model.Product
import com.nhom10.quanlybanhang.viewmodel.ProductViewModel

import android.net.Uri // Thêm
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import java.io.ByteArrayOutputStream
import android.util.Base64




// -------------------------------------------------------------

@Composable
fun AddProductScreen(
    navController: NavController,
    productViewModel: ProductViewModel
) {
    val context = LocalContext.current

    //  STATE ẢNH CỤC BỘ (Dùng Uri để chọn, sau đó chuyển Base64 trong onSave) ---

    var currentImageData by remember { mutableStateOf("") }



    // sử dụng hàm chính với ViewModel.

    // Sử dụng BaseProductScreen mới
    BaseProductScreen(
        navController = navController,
        screenTitle = "Thêm mặt hàng",
        initialProductData = null,


        onDelete = null,


        imageData = currentImageData, // Truyền Base64 hiện tại

        // Khi người dùng chọn ảnh mới, cập nhật state 'currentImageData'
        onImageSelected = { newBase64 ->
            currentImageData = newBase64
        },

        // Khi người dùng nhấn nút xóa ảnh, đặt Base64 về rỗng
        onImageRemove = {
            currentImageData = ""
        },



        onSave = { newProduct ->


            //  Chuẩn hóa dữ liệu
            val tenMatHang = newProduct.tenMatHang
            val maMatHang = newProduct.maMatHang
            val soLuongStr = newProduct.soLuong.toString().trim()
            val giaBanStr = newProduct.giaBan.toString().trim()
            val giaNhapStr = newProduct.giaNhap.toString().trim()

            // KIỂM TRA RỖNG (Sử dụng dữ liệu từ newProduct)
            if (tenMatHang.isBlank() || maMatHang.isBlank() || soLuongStr == "0.0" || giaBanStr == "0.0" || giaNhapStr == "0.0") {
                Toast.makeText(context, "Vui lòng nhập đủ thông tin (Tên, Mã, Số lượng, Giá)", Toast.LENGTH_SHORT).show()
                return@BaseProductScreen
            }

            // KIỂM TRA ĐỊNH DẠNG SỐ & SỐ ÂM (Hàm BaseProductScreen đã chuyển sang Double, kiểm tra số âm)
            if (newProduct.soLuong < 0 || newProduct.giaBan < 0 || newProduct.giaNhap < 0) {
                Toast.makeText(context, "Số lượng và Giá không được âm!", Toast.LENGTH_SHORT).show()
                return@BaseProductScreen
            }

            //  Xử lý ảnh (Đã được cập nhật Base64 từ state 'currentImageData')
            val imageDataString = currentImageData

            // Kiểm tra nếu Base64 quá lớn (Cảnh báo)
            if (imageDataString.length * 2 > 800_000) {
                Toast.makeText(context, "Cảnh báo: Ảnh quá lớn!", Toast.LENGTH_LONG).show()
            }

            //  Tạo đối tượng Product cuối cùng
            // (Đảm bảo BaseProductScreen đã xử lý việc lấy giá trị số Double)
            val finalProduct = newProduct.copy(
                imageData = imageDataString
            )

            // Gọi ViewModel để lưu
            productViewModel.addProduct(
                product = finalProduct,
                onSuccess = {
                    Toast.makeText(context, "Thêm thành công!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                onFailure = { e ->
                    Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            )
        }
    )
}
