package com.nhom10.quanlybanhang.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Tạo file lưu trữ tên là "settings"
val Context.dataStore by preferencesDataStore(name = "settings")

class ThemeStorage(private val context: Context) {
    companion object {
        val THEME_KEY = intPreferencesKey("theme_mode")
    }

    // Đọc dữ liệu (Mặc định là 0 - Theo hệ thống)
    val themeMode: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: 0
        }

    // Lưu dữ liệu
    suspend fun saveThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = mode
        }
    }
}