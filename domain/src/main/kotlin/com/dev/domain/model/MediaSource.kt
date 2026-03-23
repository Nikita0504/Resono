package com.dev.domain.model

sealed interface MediaSource {
    data class LocalOnly(
        val local: LocalMediaData,
    ) : MediaSource

    data class RemoteOnly(
        val remote: RemoteMediaData,
    ) : MediaSource

    data class Synced(
        val local: LocalMediaData,
        val remote: RemoteMediaData,
    ) : MediaSource
}

interface BaseMediaData {
    val id: String
    val name: String
    val mimeType: String
    val sizeBytes: Long
    val durationMs: Long
    val dateAddedEpochSeconds: Long?
    val dateModifiedEpochSeconds: Long?
    val relativePath: String?
}

data class LocalMediaData(
    val localUri: String,
    val fileSizeBytes: Long,
    val dateAddedEpochSeconds: Long?,
    val dateModifiedEpochSeconds: Long?,
    val relativePath: String?,
)

data class RemoteMediaData(
    val remoteId: String,
    val remoteUrl: String,
    val isDownloaded: Boolean,
)
