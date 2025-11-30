package com.nhom10.quanlybanhang.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nhom10.quanlybanhang.data.FontSizeStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FontSizeViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = FontSizeStorage(application.applicationContext)

    private val _fontSize = MutableStateFlow(16f) // default
    val fontSize: StateFlow<Float> = _fontSize

    init {
        // Load font size từ storage khi ViewModel được tạo
        viewModelScope.launch {
            val size = storage.fontSize.first()
            _fontSize.value = size
        }
    }

    // Cập nhật font size
    fun setFontSize(size: Float) {
        _fontSize.value = size
    }

    // Lưu font size vào storage
    fun saveFontSize() {
        viewModelScope.launch {
            storage.saveFontSize(_fontSize.value)
        }
    }
}
