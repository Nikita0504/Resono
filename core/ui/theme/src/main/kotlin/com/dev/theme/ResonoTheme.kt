package com.dev.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ResonoColorScheme = darkColorScheme(
    primary = Color(0xFFAEBEFF),
    onPrimary = Color(0xFF0B0D12),
    secondary = Color(0xFF89A0FF),
    background = Color(0xFF0B0D12),
    onBackground = Color(0xFFF2F4F8),
    surface = Color(0xFF151821),
    onSurface = Color(0xFFF2F4F8),
    surfaceVariant = Color(0xFF1B1F2A),
    onSurfaceVariant = Color(0xFF98A1B3),
    outline = Color(0xFF2B3242),
    error = Color(0xFFFF7F8A),
)

private val ResonoTypography = Typography(
    headlineSmall = TextStyle(
        fontSize = 28.sp,
        lineHeight = 32.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Medium,
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = TextStyle(
        fontSize = 14.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Medium,
    ),
)

private val ResonoShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
)

@Immutable
data class ResonoDimens(
    val screenPadding: androidx.compose.ui.unit.Dp = 20.dp,
    val cardPadding: androidx.compose.ui.unit.Dp = 16.dp,
    val sectionGap: androidx.compose.ui.unit.Dp = 24.dp,
    val itemGap: androidx.compose.ui.unit.Dp = 12.dp,
    val artworkSize: androidx.compose.ui.unit.Dp = 56.dp,
)

val LocalResonoDimens = staticCompositionLocalOf { ResonoDimens() }

object ResonoTheme {
    val dimens: ResonoDimens
        @Composable get() = LocalResonoDimens.current
}

@Composable
fun ResonoTheme(content: @Composable () -> Unit) {
    androidx.compose.runtime.CompositionLocalProvider(
        LocalResonoDimens provides ResonoDimens(),
    ) {
        MaterialTheme(
            colorScheme = ResonoColorScheme,
            typography = ResonoTypography,
            shapes = ResonoShapes,
            content = content,
        )
    }
}
