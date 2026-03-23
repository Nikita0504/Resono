package com.dev.local.model

data class PhotoMediaStoreRecord(
    val id: Long,
    val displayName: String,
    val title: String?,
    val mimeType: String,
    val sizeBytes: Long,
    val width: Int,
    val height: Int,
    val dateAddedEpochSeconds: Long?,
    val dateModifiedEpochSeconds: Long?,
    val relativePath: String?,
    val contentUriString: String,
)
