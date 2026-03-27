package com.dev.albumlist

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.component.ResonoCoverPickerSection
import com.dev.component.ResonoEmptyState
import com.dev.component.ResonoFormSection
import com.dev.component.ResonoMediaListItem
import com.dev.component.ResonoSectionHeader
import com.dev.component.ResonoSurfaceCard
import com.dev.domain.model.MediaFile
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AlbumListRoute(
    onCreateAlbum: () -> Unit,
    onOpenAlbum: (String) -> Unit,
    onPlayAlbum: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
) {
    val viewModel = koinViewModel<AlbumListViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AlbumListEffect.OpenCreateAlbum -> onCreateAlbum()
                is AlbumListEffect.OpenAlbumEditor -> onOpenAlbum(effect.albumId)
                is AlbumListEffect.PlayAlbum -> onPlayAlbum(effect.tracks, 0)
            }
        }
    }

    AlbumListScreen(
        state = uiState,
        onCreateAlbum = { viewModel.onIntent(AlbumListIntent.CreateAlbum) },
        onOpenAlbum = { viewModel.onIntent(AlbumListIntent.OpenAlbum(it)) },
        onPlayAlbum = { viewModel.onIntent(AlbumListIntent.PlayAlbum(it)) },
        onDeleteAlbum = { viewModel.onIntent(AlbumListIntent.DeleteAlbum(it)) },
    )
}

@Composable
fun AlbumListScreen(
    state: AlbumListUiState,
    onCreateAlbum: () -> Unit,
    onOpenAlbum: (String) -> Unit,
    onPlayAlbum: (String) -> Unit,
    onDeleteAlbum: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header") {
            ResonoSectionHeader(
                title = "Albums",
                subtitle = "User albums live inside the music layer and reuse the same track library.",
                action = {
                    TextButton(onClick = onCreateAlbum) {
                        Text("Create")
                    }
                },
            )
        }

        if (state.albums.isEmpty()) {
            item(key = "empty") {
                ResonoEmptyState(
                    title = "No albums yet",
                    modifier = Modifier.padding(top = 24.dp),
                )
            }
        } else {
            items(state.albums, key = { it.id }) { album ->
                ResonoMediaListItem(
                    title = album.title,
                    subtitle = album.description.orEmpty().ifBlank { "Custom album" },
                    meta = "${album.trackCount} tracks",
                    artworkUri = album.artworkUri,
                    onClick = { onOpenAlbum(album.id) },
                    trailing = {
                        Row {
                            TextButton(onClick = { onPlayAlbum(album.id) }) {
                                Text("Play")
                            }
                            TextButton(onClick = { onOpenAlbum(album.id) }) {
                                Text("Edit")
                            }
                            TextButton(onClick = { onDeleteAlbum(album.id) }) {
                                Text("Delete")
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun AlbumEditorRoute(
    onClose: () -> Unit,
    onPlayAlbum: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
) {
    val viewModel = koinViewModel<AlbumEditorViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                AlbumEditorEffect.CloseEditor -> onClose()
            }
        }
    }

    AlbumEditorScreen(
        state = uiState,
        onIntent = viewModel::onIntent,
        onPlayAlbum = onPlayAlbum,
    )
}

@Composable
fun AlbumEditorScreen(
    state: AlbumEditorUiState,
    onIntent: (AlbumEditorIntent) -> Unit,
    onPlayAlbum: (tracks: List<MediaFile.Audio>, startIndex: Int) -> Unit,
) {
    val context = LocalContext.current
    var confirmDelete by remember { mutableStateOf(false) }
    val coverPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            onIntent(AlbumEditorIntent.ArtworkUriChanged(uri.toString()))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "header") {
            ResonoSectionHeader(
                title = if (state.albumId == null) "Create album" else "Edit album",
                subtitle = if (state.existingAlbum == null) {
                    "Build a custom album from the current track library."
                } else {
                    "${state.selectedTrackIds.size} selected tracks"
                },
                action = {
                    if (state.existingAlbum != null) {
                        TextButton(onClick = { confirmDelete = true }) {
                            Text("Delete")
                        }
                    }
                    TextButton(
                        onClick = {
                            if (state.selectedTrackIds.isNotEmpty()) {
                                val selectedTracks = state.allTracks.filter { it.id in state.selectedTrackIds }
                                if (selectedTracks.isNotEmpty()) {
                                    onPlayAlbum(selectedTracks, 0)
                                }
                            }
                        },
                    ) {
                        Text("Play")
                    }
                    TextButton(onClick = { onIntent(AlbumEditorIntent.Save) }) {
                        Text("Save")
                    }
                },
            )
        }

        item(key = "fields") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ResonoCoverPickerSection(
                    artworkUri = state.artworkUri,
                    onPickCover = { coverPicker.launch(arrayOf("image/*")) },
                    onClearCover = { onIntent(AlbumEditorIntent.ArtworkUriChanged("")) },
                )
                ResonoFormSection(title = "Main") {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { onIntent(AlbumEditorIntent.TitleChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Album title") },
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { onIntent(AlbumEditorIntent.DescriptionChanged(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Description") },
                    )
                }
            }
        }

        item(key = "tracks-header") {
            ResonoSectionHeader(
                title = "Album tracks",
                subtitle = "${state.selectedTrackIds.size} selected",
            )
        }

        items(state.allTracks, key = { it.id }) { track ->
            ResonoSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(AlbumEditorIntent.ToggleTrack(track.id)) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Checkbox(
                        checked = track.id in state.selectedTrackIds,
                        onCheckedChange = { onIntent(AlbumEditorIntent.ToggleTrack(track.id)) },
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title ?: track.name,
                        )
                        Text(
                            text = listOf(track.artist, track.album)
                                .filter { it.isNotBlank() }
                                .joinToString(" • "),
                        )
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete album") },
            text = { Text("This album will be removed. Tracks stay in the library.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onIntent(AlbumEditorIntent.Delete)
                        confirmDelete = false
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancel")
                }
            },
        )
    }
}
