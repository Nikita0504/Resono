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
    val durationMs: Long
    val mimeType: String
}

data class LocalMediaData(
    val localUri: String,
    val fileSize: Long,
    val dateAdded: Long,
)

data class RemoteMediaData(
    val remoteId: String,
    val remoteUrl: String,
    val isDownloaded: Boolean,
)