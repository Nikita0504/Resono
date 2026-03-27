package com.dev.tracklist

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dev.component.ResonoEmptyState
import com.dev.component.ResonoMediaListItem
import com.dev.component.ResonoMetadataEditorScreen
import com.dev.component.ResonoSectionHeader
import com.dev.domain.model.AudioMetadataPatch
import com.dev.domain.model.MediaFile

@Composable
fun TrackListScreen(
    tracks: List<MediaFile.Audio>,
    hiddenTracks: List<MediaFile.Audio>,
    showHiddenTracks: Boolean,
    onOpenAlbums: () -> Unit,
    onTrackClick: (Int) -> Unit,
    onShowHiddenChanged: (Boolean) -> Unit,
    onToggleFavorite: (trackId: String, favorite: Boolean) -> Unit,
    onHideTrack: (trackId: String) -> Unit,
    onRestoreTrack: (trackId: String) -> Unit,
    onEditMetadata: (trackId: String, metadata: AudioMetadataPatch) -> Unit,
) {
    val context = LocalContext.current
    var editTarget by remember { mutableStateOf<MediaFile.Audio?>(null) }
    var titleDraft by remember { mutableStateOf("") }
    var artistDraft by remember { mutableStateOf("") }
    var albumDraft by remember { mutableStateOf("") }
    var artworkUriDraft by remember { mutableStateOf("") }
    var trackNumberDraft by remember { mutableStateOf("") }
    var yearDraft by remember { mutableStateOf("") }

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

    val currentTracks = if (showHiddenTracks) hiddenTracks else tracks

    if (editTarget != null) {
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
            onBack = { editTarget = null },
            onSave = {
                val target = editTarget ?: return@ResonoMetadataEditorScreen
                onEditMetadata(
                    target.id,
                    AudioMetadataPatch(
                        title = titleDraft,
                        artist = artistDraft,
                        album = albumDraft,
                        albumArtUri = artworkUriDraft,
                        trackNumber = trackNumberDraft.toIntOrNull(),
                        year = yearDraft.toIntOrNull(),
                    ),
                )
                editTarget = null
            },
            onReset = {
                val target = editTarget ?: return@ResonoMetadataEditorScreen
                onEditMetadata(target.id, AudioMetadataPatch())
                editTarget = null
            },
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "tabs") {
            PrimaryTabRow(selectedTabIndex = if (showHiddenTracks) 1 else 0) {
                Tab(
                    selected = !showHiddenTracks,
                    onClick = { onShowHiddenChanged(false) },
                    text = { Text("Tracks (${tracks.size})") },
                )
                Tab(
                    selected = showHiddenTracks,
                    onClick = { onShowHiddenChanged(true) },
                    text = { Text("Hidden (${hiddenTracks.size})") },
                )
            }
        }

        item(key = "header") {
            ResonoSectionHeader(
                title = if (showHiddenTracks) "Hidden Tracks" else "Tracks",
                subtitle = if (showHiddenTracks) {
                    "Hidden tracks stay in the library and can be restored here."
                } else {
                    "Local tracks: ${tracks.size}. Albums stay inside the music layer."
                },
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                action = {
                    if (!showHiddenTracks) {
                        TextButton(onClick = onOpenAlbums) {
                            Text("Albums")
                        }
                    }
                },
            )
        }

        if (currentTracks.isEmpty()) {
            item(key = "empty") {
                ResonoEmptyState(
                    title = if (showHiddenTracks) "No hidden tracks" else "Track list is empty",
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
            return@LazyColumn
        }

        itemsIndexed(currentTracks, key = { _, track -> track.id }) { index, track ->
            var menuExpanded by remember(track.id) { mutableStateOf(false) }
            val isClickable = !showHiddenTracks
            ResonoMediaListItem(
                title = track.title ?: track.name,
                subtitle = listOf(track.artist, track.album)
                    .filter { it.isNotBlank() }
                    .joinToString(" • "),
                meta = buildString {
                    append(track.durationMs.toDurationLabel())
                    track.year?.let { append(" • $it") }
                    if (!showHiddenTracks) append(" • ${track.sizeBytes.toSizeLabel()}")
                },
                artworkUri = track.albumArtUri,
                onClick = if (isClickable) ({ onTrackClick(index) }) else null,
                trailing = {
                    Box {
                        TextButton(onClick = { menuExpanded = true }) {
                            Text("More")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            if (showHiddenTracks) {
                                DropdownMenuItem(
                                    text = { Text("Restore") },
                                    onClick = {
                                        menuExpanded = false
                                        onRestoreTrack(track.id)
                                    },
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text(if (track.isFavorite) "Remove favorite" else "Add favorite") },
                                    onClick = {
                                        menuExpanded = false
                                        onToggleFavorite(track.id, !track.isFavorite)
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Edit metadata") },
                                    onClick = {
                                        menuExpanded = false
                                        editTarget = track
                                        titleDraft = track.title ?: track.name
                                        artistDraft = track.artist
                                        albumDraft = track.album
                                        artworkUriDraft = track.albumArtUri.orEmpty()
                                        trackNumberDraft = track.trackNumber?.toString().orEmpty()
                                        yearDraft = track.year?.toString().orEmpty()
                                    },
                                )
                                DropdownMenuItem(
                                    text = { Text("Hide") },
                                    onClick = {
                                        menuExpanded = false
                                        onHideTrack(track.id)
                                    },
                                )
                            }
                        }
                    }
                },
            )
        }
    }
}

private fun Long.toSizeLabel(): String {
    if (this <= 0L) return "0 B"
    val kilobytes = this / 1024.0
    if (kilobytes < 1024.0) return "${"%.1f".format(kilobytes)} KB"
    val megabytes = kilobytes / 1024.0
    return "${"%.1f".format(megabytes)} MB"
}

private fun Long.toDurationLabel(): String {
    if (this <= 0L) return "00:00"
    val totalSeconds = this / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
