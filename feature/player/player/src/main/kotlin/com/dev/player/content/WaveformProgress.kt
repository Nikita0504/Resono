package com.dev.player.content

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun WaveformProgress(
    progress: Float,
    bars: List<Float>,
    modifier: Modifier = Modifier,
    activeColor: Color,
    inactiveColor: Color,
) {
    val safeProgress = progress.coerceIn(0f, 1f)
    val playedBars = (bars.size * safeProgress).toInt()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
    ) {
        if (bars.isEmpty()) return@Canvas
        val barWidth = size.width / bars.size
        bars.forEachIndexed { index, amp ->
            val color = if (index <= playedBars) activeColor else inactiveColor
            val height = size.height * amp
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = index * barWidth,
                    y = (size.height - height) / 2f,
                ),
                size = Size(
                    width = (barWidth * 0.72f).coerceAtLeast(1f),
                    height = height.coerceAtLeast(2f),
                ),
                cornerRadius = CornerRadius(999f, 999f),
            )
        }
    }
}
