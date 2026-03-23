package com.dev.local.model

data class AudioMediaStoreRecord(
    val id: Long,
    val displayName: String,
    val title: String?,
    val mimeType: String,
    val sizeBytes: Long,
    val durationMs: Long,
    val artist: String?,
    val album: String?,
    val trackNumber: Int?,
    val year: Int?,
    val dateAddedEpochSeconds: Long?,
    val dateModifiedEpochSeconds: Long?,
    val relativePath: String?,
    val contentUriString: String,
    val albumArtUriString: String?,
)
