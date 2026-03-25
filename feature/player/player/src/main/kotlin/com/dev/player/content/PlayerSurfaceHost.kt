package com.dev.player.content

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.domain.model.PlayerState

private val MiniCoverSize = 44.dp
private val ExpandedArtworkTop = 92.dp
private val MiniArtworkStart = 12.dp
private val MiniArtworkTop = 14.dp
private const val ExpandedStartThreshold = 0.22f

@Composable
fun PlayerSurfaceHost(
    playerState: PlayerState,
    progress: Float,
    miniBarHeight: Dp,
    onExpand: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
) {
    val currentItem = playerState.currentItem ?: return
    val mediaFile = currentItem.mediaFile
    val clampedProgress = progress.coerceIn(0f, 1f)

    var sliderPosition by remember(mediaFile.id) { mutableFloatStateOf(playerState.progress) }
    var isUserSeeking by remember { mutableStateOf(false) }
    val density = LocalDensity.current

    LaunchedEffect(playerState.positionMs, playerState.durationMs, mediaFile.id, isUserSeeking) {
        if (!isUserSeeking) sliderPosition = playerState.progress.coerceIn(0f, 1f)
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val expandedCover = (maxWidth * 0.72f).coerceAtMost(320.dp)

        val expandedCoverPx = with(density) { expandedCover.toPx() }
        val miniCoverPx = with(density) { MiniCoverSize.toPx() }
        val miniStartPx = with(density) { MiniArtworkStart.toPx() }
        val miniTopPx = with(density) { MiniArtworkTop.toPx() }
        val expandedTopPx = with(density) { ExpandedArtworkTop.toPx() }
        val expandedStartPx = ((constraints.maxWidth - expandedCoverPx) * 0.5f).coerceAtLeast(0f)

        val artworkScale = lerp(miniCoverPx / expandedCoverPx, 1f, clampedProgress)
        val artworkX = lerp(miniStartPx, expandedStartPx, clampedProgress)
        val artworkY = lerp(miniTopPx, expandedTopPx, clampedProgress)

        val miniAlpha = (1f - clampedProgress * 1.2f).coerceIn(0f, 1f)
        val expandedAlpha = ((clampedProgress - ExpandedStartThreshold) / (1f - ExpandedStartThreshold)).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(miniBarHeight)
                .alpha(miniAlpha)
                .clickable(enabled = clampedProgress < 0.2f, onClick = onExpand),
        )

        PlayerArtwork(
            modifier = Modifier
                .size(expandedCover)
                .graphicsLayer {
                    transformOrigin = TransformOrigin(0f, 0f)
                    translationX = artworkX
                    translationY = artworkY
                    scaleX = artworkScale
                    scaleY = artworkScale
                },
            progress = clampedProgress,
            artworkModel = mediaFile.bestArtworkModel(),
        )

        MiniPlayerContent(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = miniAlpha },
            title = mediaFile.bestTitle().limitChars(30),
            subtitle = mediaFile.bestSubtitle().limitChars(40),
            isPlaying = playerState.isPlaying,
            hasNext = playerState.hasNext,
            enabled = clampedProgress < 0.25f,
            onExpand = onExpand,
            onPlayPause = onPlayPause,
            onNext = onNext,
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = ExpandedArtworkTop + expandedCover + 24.dp)
                .graphicsLayer { alpha = expandedAlpha },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Text(
                text = mediaFile.bestTitle(),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = mediaFile.bestSubtitle(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Slider(
                value = sliderPosition,
                onValueChange = {
                    isUserSeeking = true
                    sliderPosition = it
                },
                onValueChangeFinished = {
                    isUserSeeking = false
                    val duration = playerState.durationMs
                    if (duration > 0L) onSeekTo((duration * sliderPosition).toLong())
                },
            )

            Text(
                text = "${playerState.positionMs.toDurationLabel()} / ${playerState.durationMs.toDurationLabel()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.Start),
            )

            ExpandedPlayerControls(
                isPlaying = playerState.isPlaying,
                hasNext = playerState.hasNext,
                hasPrevious = playerState.hasPrevious,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}
