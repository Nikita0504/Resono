package com.dev.domain.model

sealed interface MediaFile : BaseMediaData {

    val source: MediaSource

    val localUri: String?
        get() = when (val s = source) {
            is MediaSource.LocalOnly -> s.local.localUri
            is MediaSource.Synced   -> s.local.localUri
            is MediaSource.RemoteOnly -> null
        }

    val remoteUrl: String?
        get() = when (val s = source) {
            is MediaSource.RemoteOnly -> s.remote.remoteUrl
            is MediaSource.Synced     -> s.remote.remoteUrl
            is MediaSource.LocalOnly  -> null
        }

    val isAvailableOffline: Boolean
        get() = source is MediaSource.LocalOnly || source is MediaSource.Synced

    data class Audio(
        override val id: String,
        override val name: String,
        override val durationMs: Long,
        override val mimeType: String,
        override val source: MediaSource,
        val artist: String,
        val album: String,
        val albumArtUri: String?,
        val trackNumber: Int? = null,
    ) : MediaFile

    data class Video(
        override val id: String,
        override val name: String,
        override val durationMs: Long,
        override val mimeType: String,
        override val source: MediaSource,
        val width: Int,
        val height: Int,
        val thumbnailUri: String?,
    ) : MediaFile

    data class Photo(
        override val id: String,
        override val name: String,
        override val durationMs: Long = 0L,
        override val mimeType: String,
        override val source: MediaSource,
        val width: Int,
        val height: Int,
    ) : MediaFile
}
