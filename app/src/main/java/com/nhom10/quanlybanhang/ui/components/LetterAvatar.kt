package com.nhom10.quanlybanhang.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun LetterAvatar(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val letter = remember(name) {
        if (name.isNotBlank()) {
            val parts = name.trim().split("\\s+".toRegex())
            if (parts.isNotEmpty()) {
                parts.last().first().toString().uppercase(Locale.ROOT)
            } else {
                name.first().toString().uppercase(Locale.ROOT)
            }
        } else {
            "?"
        }
    }

    val backgroundColor = remember(name) {
        generateColorByName(name)
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter,
            color = Color.White,
            fontSize = (size.value / 2.5).sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun generateColorByName(name: String): Color {
    val hash = name.hashCode()
    val colors = listOf(
        Color(0xFFF44336), Color(0xFFE91E63), Color(0xFF9C27B0),
        Color(0xFF673AB7), Color(0xFF3F51B5), Color(0xFF2196F3),
        Color(0xFF03A9F4), Color(0xFF00BCD4), Color(0xFF009688),
        Color(0xFF4CAF50), Color(0xFF8BC34A), Color(0xFFFF9800),
        Color(0xFFFF5722), Color(0xFF795548), Color(0xFF607D8B)
    )
    val index = Math.abs(hash) % colors.size
    return colors[index]
}