package com.dev.database.model

data class AudioAlbumRecord(
    val id: String,
    val title: String,
    val description: String?,
    val artworkUri: String?,
    val trackIds: List<String>,
)
