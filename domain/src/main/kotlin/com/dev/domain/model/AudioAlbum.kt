package com.dev.domain.model

data class AudioAlbum(
    val id: String,
    val title: String,
    val description: String?,
    val artworkUri: String?,
    val trackIds: List<String>,
    val tracks: List<MediaFile.Audio>,
) {
    val trackCount: Int
        get() = tracks.size
}
