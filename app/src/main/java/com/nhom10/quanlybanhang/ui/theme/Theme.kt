package com.nhom10.quanlybanhang.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.nhom10.quanlybanhang.viewmodel.FontSizeViewModel
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun QuanLyBanHangTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = true,
    fontSizeViewModel: FontSizeViewModel? = null, // Thêm tham số
    content: @Composable () -> Unit
) {
    // Lấy fontSize từ ViewModel, nếu null thì dùng 16f làm mặc định
    val fontSizeState = fontSizeViewModel?.fontSize?.collectAsState()
    val fontSize = fontSizeState?.value ?: 16f


    // Tạo Typography tùy chỉnh theo fontSize
    val appTypography = Typography.copy(
        bodyLarge = Typography.bodyLarge.copy(fontSize = fontSize.sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = fontSize.sp),
        titleLarge = Typography.titleLarge.copy(fontSize = fontSize.sp),
        titleMedium = Typography.titleMedium.copy(fontSize = fontSize.sp)
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography, // Dùng Typography tùy chỉnh
        content = content
    )
}
