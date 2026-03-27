package com.dev.domain.model

data class AudioMetadataPatch(
    val title: String? = null,
    val artist: String? = null,
    val album: String? = null,
    val albumArtUri: String? = null,
    val trackNumber: Int? = null,
    val year: Int? = null,
)
