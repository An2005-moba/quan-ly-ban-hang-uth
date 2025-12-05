package com.nhom10.quanlybanhang.utils

import java.text.Normalizer
import java.util.regex.Pattern

object SearchHelper {
    // Hàm loại bỏ dấu tiếng Việt (Giữ nguyên)
    fun unAccent(s: String): String {
        val temp = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace('đ', 'd').replace('Đ', 'D')
    }

    // Hàm kiểm tra khớp dữ liệu
    fun isMatch(source: String, query: String): Boolean {
        if (query.isBlank()) return true

        val sourceClean = unAccent(source.lowercase())
        val queryClean = unAccent(query.lowercase())


        val keywords = queryClean.split(" ").filter { it.isNotBlank() }
        val isWordMatch = keywords.all { keyword -> sourceClean.contains(keyword) }

        if (isWordMatch) return true


        val sourceNoSpace = sourceClean.replace(" ", "")
        val queryNoSpace = queryClean.replace(" ", "")

        return sourceNoSpace.contains(queryNoSpace)
    }
}