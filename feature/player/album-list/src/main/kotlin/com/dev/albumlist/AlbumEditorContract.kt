package com.dev.albumlist

import com.dev.domain.model.AudioAlbum
import com.dev.domain.model.MediaFile

data class AlbumEditorUiState(
    val albumId: String? = null,
    val title: String = "",
    val description: String = "",
    val artworkUri: String = "",
    val allTracks: List<MediaFile.Audio> = emptyList(),
    val selectedTrackIds: Set<String> = emptySet(),
    val existingAlbum: AudioAlbum? = null,
)

sealed interface AlbumEditorIntent {
    data class TitleChanged(val value: String) : AlbumEditorIntent
    data class DescriptionChanged(val value: String) : AlbumEditorIntent
    data class ArtworkUriChanged(val value: String) : AlbumEditorIntent
    data class ToggleTrack(val trackId: String) : AlbumEditorIntent
    data object Save : AlbumEditorIntent
    data object Delete : AlbumEditorIntent
}

sealed interface AlbumEditorEffect {
    data object CloseEditor : AlbumEditorEffect
}
