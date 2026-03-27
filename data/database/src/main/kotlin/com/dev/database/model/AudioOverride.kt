package com.dev.database.model

data class AudioOverride(
    val audioId: String,
    val isHidden: Boolean,
    val isFavorite: Boolean,
    val customTitle: String?,
    val customArtist: String?,
    val customAlbum: String?,
    val customAlbumArtUri: String?,
    val customTrackNumber: Int?,
    val customYear: Int?,
    val playbackPositionMs: Long?,
)
