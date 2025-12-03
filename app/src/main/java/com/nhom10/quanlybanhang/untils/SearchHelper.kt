package com.nhom10.quanlybanhang.utils // Hoặc package hiện tại của bạn

import java.text.Normalizer
import java.util.regex.Pattern

object SearchHelper {
    // Hàm loại bỏ dấu tiếng Việt (Ví dụ: "Gà Rán" -> "ga ran")
    fun unAccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
    }

    // Hàm kiểm tra khớp dữ liệu
    // source: Chuỗi gốc (ví dụ: Tên sản phẩm)
    // query: Từ khóa tìm kiếm (ví dụ: user nhập "ga ran")
    fun isMatch(source: String, query: String): Boolean {
        if (query.isBlank()) return true

        // 1. Chuyển cả 2 về dạng không dấu, chữ thường
        val sourceClean = unAccent(source.lowercase())
        val queryClean = unAccent(query.lowercase())

        // 2. Tách từ khóa tìm kiếm thành từng từ (ví dụ: "ga ran" -> ["ga", "ran"])
        val keywords = queryClean.split(" ").filter { it.isNotBlank() }

        // 3. Kiểm tra xem TẤT CẢ các từ khóa có xuất hiện trong chuỗi gốc không
        // Ví dụ: Nhập "bo huc" vẫn tìm ra "Nước tăng lực Bò Húc"
        return keywords.all { keyword -> sourceClean.contains(keyword) }
    }
}