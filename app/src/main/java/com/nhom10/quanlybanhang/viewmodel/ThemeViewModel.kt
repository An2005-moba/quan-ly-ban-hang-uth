package com.nhom10.quanlybanhang.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.ThemeStorage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// Dùng AndroidViewModel để lấy Context (Application)
class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = ThemeStorage(application)

    // Biến này để UI và MainActivity lắng nghe
    val themeMode: StateFlow<Int> = storage.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    fun setTheme(mode: Int) {
        viewModelScope.launch {
            storage.saveThemeMode(mode)
        }
    }
}