package com.dev.player

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.model.PlayerState
import com.dev.player.content.PlayerSurfaceHost
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

private enum class PlayerSurfaceAnchor {
    Mini,
    Expanded,
}

private val MiniBarHeight = 72.dp
private val MiniGap = 8.dp

@Composable
fun PlayerSheetRoute(
    modifier: Modifier = Modifier,
    playerState: PlayerState,
    isVisible: Boolean,
    isExpanded: Boolean,
    bottomBarHeightPx: Int,
    onExpandedChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    waveformSamples: List<Float>,
    displayTitle: String,
    displayArtist: String,
    displayAlbum: String,
    displayArtworkUri: String?,
    displayTrackNumber: Int?,
    displayYear: Int?,
    isFavorite: Boolean,
    onToggleFavorite: (Boolean) -> Unit,
    onEditMetadata: (AudioMetadataPatch) -> Unit,
    onHide: () -> Unit,
) {
    PlayerSheetHost(
        modifier = modifier,
        playerState = playerState,
        isVisible = isVisible,
        isExpanded = isExpanded,
        bottomBarHeightPx = bottomBarHeightPx,
        onExpandedChange = onExpandedChange,
        onProgressChange = onProgressChange,
        onPlayPause = onPlayPause,
        onNext = onNext,
        onPrevious = onPrevious,
        onSeekTo = onSeekTo,
        waveformSamples = waveformSamples,
        displayTitle = displayTitle,
        displayArtist = displayArtist,
        displayAlbum = displayAlbum,
        displayArtworkUri = displayArtworkUri,
        displayTrackNumber = displayTrackNumber,
        displayYear = displayYear,
        isFavorite = isFavorite,
        onToggleFavorite = onToggleFavorite,
        onEditMetadata = onEditMetadata,
        onHide = onHide,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlayerSheetHost(
    modifier: Modifier,
    playerState: PlayerState,
    isVisible: Boolean,
    isExpanded: Boolean,
    bottomBarHeightPx: Int,
    onExpandedChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    waveformSamples: List<Float>,
    displayTitle: String,
    displayArtist: String,
    displayAlbum: String,
    displayArtworkUri: String?,
    displayTrackNumber: Int?,
    displayYear: Int?,
    isFavorite: Boolean,
    onToggleFavorite: (Boolean) -> Unit,
    onEditMetadata: (AudioMetadataPatch) -> Unit,
    onHide: () -> Unit,
) {
    if (!isVisible || playerState.currentItem == null) {
        LaunchedEffect(Unit) { onProgressChange(0f) }
        return
    }

    val onProgressUpdated by rememberUpdatedState(onProgressChange)
    val onExpandedUpdated by rememberUpdatedState(onExpandedChange)
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { clip = true },
    ) {
        val containerHeightPx = constraints.maxHeight.toFloat()
        val miniHeightPx = with(density) { MiniBarHeight.toPx() }
        val miniGapPx = with(density) { MiniGap.toPx() }
        val miniAnchorPx =
            (containerHeightPx - miniHeightPx - bottomBarHeightPx - miniGapPx)
                .coerceAtLeast(0f)

        val draggableState = remember {
            AnchoredDraggableState<PlayerSurfaceAnchor>(
                initialValue = if (isExpanded) PlayerSurfaceAnchor.Expanded else PlayerSurfaceAnchor.Mini,
                positionalThreshold = { distance -> distance * 0.5f },
                velocityThreshold = { with(density) { 180.dp.toPx() } },
                snapAnimationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                    dampingRatio = Spring.DampingRatioNoBouncy,
                ),
                decayAnimationSpec = exponentialDecay(),
            )
        }

        val anchors = remember(miniAnchorPx) {
            DraggableAnchors {
                PlayerSurfaceAnchor.Expanded at 0f
                PlayerSurfaceAnchor.Mini at miniAnchorPx
            }
        }

        LaunchedEffect(anchors) {
            draggableState.updateAnchors(anchors)
        }

        LaunchedEffect(isExpanded) {
            val target = if (isExpanded) PlayerSurfaceAnchor.Expanded else PlayerSurfaceAnchor.Mini
            if (draggableState.targetValue != target) {
                draggableState.animateTo(target)
            }
        }

        LaunchedEffect(draggableState, miniAnchorPx) {
            snapshotFlow {
                val offset = draggableState.offset
                if (offset.isNaN()) 0f
                else ((miniAnchorPx - offset) / miniAnchorPx.coerceAtLeast(1f)).coerceIn(0f, 1f)
            }
                .distinctUntilChanged()
                .collect { progress ->
                    onProgressUpdated(progress)
                }
        }

        LaunchedEffect(draggableState) {
            snapshotFlow { draggableState.settledValue }
                .map { it == PlayerSurfaceAnchor.Expanded }
                .distinctUntilChanged()
                .collect { expanded ->
                    onExpandedUpdated(expanded)
                }
        }

        val offsetPx = runCatching { draggableState.requireOffset() }.getOrDefault(miniAnchorPx)
        val progress by remember(offsetPx, miniAnchorPx) {
            derivedStateOf {
                ((miniAnchorPx - offsetPx) / miniAnchorPx.coerceAtLeast(1f)).coerceIn(0f, 1f)
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetPx.roundToInt()) }
                .anchoredDraggable(
                    state = draggableState,
                    orientation = Orientation.Vertical,
                ),
            shape = RoundedCornerShape(lerp(20.dp, 0.dp, progress)),
            tonalElevation = lerp(8.dp, 0.dp, progress),
            color = MaterialTheme.colorScheme.surface,
        ) {
            PlayerSurfaceHost(
                playerState = playerState,
                progress = progress,
                miniBarHeight = MiniBarHeight,
                onExpand = { onExpandedUpdated(true) },
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onSeekTo = onSeekTo,
                waveformSamples = waveformSamples,
                displayTitle = displayTitle,
                displayArtist = displayArtist,
                displayAlbum = displayAlbum,
                displayArtworkUri = displayArtworkUri,
                displayTrackNumber = displayTrackNumber,
                displayYear = displayYear,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                onEditMetadata = onEditMetadata,
                onHide = onHide,
            )
        }
    }
}
