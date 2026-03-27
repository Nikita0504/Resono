package com.dev.albumlist

import com.dev.domain.model.AudioAlbum
import com.dev.domain.model.MediaFile

data class AlbumListUiState(
    val albums: List<AudioAlbum> = emptyList(),
)

sealed interface AlbumListIntent {
    data object CreateAlbum : AlbumListIntent
    data class OpenAlbum(val albumId: String) : AlbumListIntent
    data class PlayAlbum(val albumId: String) : AlbumListIntent
    data class DeleteAlbum(val albumId: String) : AlbumListIntent
}

sealed interface AlbumListEffect {
    data object OpenCreateAlbum : AlbumListEffect
    data class OpenAlbumEditor(val albumId: String) : AlbumListEffect
    data class PlayAlbum(val tracks: List<MediaFile.Audio>) : AlbumListEffect
}
