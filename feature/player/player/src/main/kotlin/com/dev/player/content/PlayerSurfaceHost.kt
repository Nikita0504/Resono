package com.dev.player.content

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dev.component.ResonoMetadataEditorScreen
import com.dev.domain.model.AudioMetadataPatch
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
    displayTitle: String,
    displayArtist: String,
    displayAlbum: String,
    displayArtworkUri: String?,
    displayTrackNumber: Int?,
    displayYear: Int?,
    isFavorite: Boolean,
    onExpand: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    waveformSamples: List<Float>,
    onToggleFavorite: (Boolean) -> Unit,
    onEditMetadata: (AudioMetadataPatch) -> Unit,
    onHide: () -> Unit,
) {
    val context = LocalContext.current
    val currentItem = playerState.currentItem ?: return
    val mediaFile = currentItem.mediaFile
    val clampedProgress = progress.coerceIn(0f, 1f)

    var sliderPosition by remember(mediaFile.id) { mutableFloatStateOf(playerState.progress) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var isRenameDialogVisible by remember { mutableStateOf(false) }
    var titleDraft by remember(mediaFile.id) {
        mutableStateOf(displayTitle)
    }
    var artistDraft by remember(mediaFile.id) { mutableStateOf(displayArtist) }
    var albumDraft by remember(mediaFile.id) { mutableStateOf(displayAlbum) }
    var artworkUriDraft by remember(mediaFile.id) { mutableStateOf(displayArtworkUri.orEmpty()) }
    var trackNumberDraft by remember(mediaFile.id) { mutableStateOf(displayTrackNumber?.toString().orEmpty()) }
    var yearDraft by remember(mediaFile.id) { mutableStateOf(displayYear?.toString().orEmpty()) }
    val density = LocalDensity.current
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            artworkUriDraft = uri.toString()
        }
    }

    LaunchedEffect(playerState.positionMs, playerState.durationMs, mediaFile.id, isUserSeeking) {
        if (!isUserSeeking) sliderPosition = playerState.progress.coerceIn(0f, 1f)
    }
    LaunchedEffect(displayTitle, mediaFile.id) {
        if (!isRenameDialogVisible) titleDraft = displayTitle
    }
    LaunchedEffect(displayArtist, mediaFile.id) {
        if (!isRenameDialogVisible) artistDraft = displayArtist
    }
    LaunchedEffect(displayAlbum, mediaFile.id) {
        if (!isRenameDialogVisible) albumDraft = displayAlbum
    }
    LaunchedEffect(displayArtworkUri, mediaFile.id) {
        if (!isRenameDialogVisible) artworkUriDraft = displayArtworkUri.orEmpty()
    }
    LaunchedEffect(displayTrackNumber, mediaFile.id) {
        if (!isRenameDialogVisible) trackNumberDraft = displayTrackNumber?.toString().orEmpty()
    }
    LaunchedEffect(displayYear, mediaFile.id) {
        if (!isRenameDialogVisible) yearDraft = displayYear?.toString().orEmpty()
    }

    if (isRenameDialogVisible) {
        ResonoMetadataEditorScreen(
            title = "Edit track metadata",
            titleDraft = titleDraft,
            onTitleDraftChange = { titleDraft = it },
            artistDraft = artistDraft,
            onArtistDraftChange = { artistDraft = it },
            albumDraft = albumDraft,
            onAlbumDraftChange = { albumDraft = it },
            artworkUriDraft = artworkUriDraft,
            onPickCover = { coverPicker.launch(arrayOf("image/*")) },
            onClearCover = { artworkUriDraft = "" },
            trackNumberDraft = trackNumberDraft,
            onTrackNumberDraftChange = { trackNumberDraft = it },
            yearDraft = yearDraft,
            onYearDraftChange = { yearDraft = it },
            onBack = { isRenameDialogVisible = false },
            onSave = {
                onEditMetadata(
                    AudioMetadataPatch(
                        title = titleDraft,
                        artist = artistDraft,
                        album = albumDraft,
                        albumArtUri = artworkUriDraft,
                        trackNumber = trackNumberDraft.toIntOrNull(),
                        year = yearDraft.toIntOrNull(),
                    ),
                )
                isRenameDialogVisible = false
            },
            onReset = {
                onEditMetadata(AudioMetadataPatch())
                isRenameDialogVisible = false
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        )
        return
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
            artworkModel = displayArtworkUri.takeIf { !it.isNullOrBlank() } ?: mediaFile.bestArtworkModel(),
        )

        MiniPlayerContent(
            modifier = Modifier
                .fillMaxWidth()
                .graphicsLayer { alpha = miniAlpha },
            title = displayTitle.limitChars(30),
            subtitle = buildAudioSubtitle(displayArtist, displayAlbum).limitChars(40),
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
                text = displayTitle,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = buildAudioSubtitle(displayArtist, displayAlbum),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            WaveformProgress(
                progress = sliderPosition,
                bars = waveformSamples,
                activeColor = MaterialTheme.colorScheme.primary,
                inactiveColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.24f),
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
                isFavorite = isFavorite,
                onPlayPause = onPlayPause,
                onNext = onNext,
                onPrevious = onPrevious,
                onToggleFavorite = {
                    onToggleFavorite(!isFavorite)
                },
                onRename = {
                    isRenameDialogVisible = true
                },
                onHide = onHide,
            )
        }
    }
}

private fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

private fun buildAudioSubtitle(artist: String, album: String): String {
    val a = artist.trim()
    val b = album.trim()
    return when {
        a.isNotEmpty() && b.isNotEmpty() -> "$a • $b"
        a.isNotEmpty() -> a
        b.isNotEmpty() -> b
        else -> "Unknown artist"
    }
}
