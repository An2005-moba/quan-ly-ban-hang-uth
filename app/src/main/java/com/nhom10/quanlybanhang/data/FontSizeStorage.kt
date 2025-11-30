package com.nhom10.quanlybanhang.data


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Tạo file lưu trữ tên là "font_settings"
val Context.fontDataStore by preferencesDataStore(name = "font_settings")

class FontSizeStorage(private val context: Context) {
    companion object {
        val FONT_SIZE_KEY = floatPreferencesKey("font_size")
    }

    // Đọc dữ liệu (Mặc định là 16f)
    val fontSize: Flow<Float> = context.fontDataStore.data
        .map { preferences ->
            preferences[FONT_SIZE_KEY] ?: 16f
        }

    // Lưu dữ liệu
    suspend fun saveFontSize(size: Float) {
        context.fontDataStore.edit { preferences ->
            preferences[FONT_SIZE_KEY] = size
        }
    }
}
