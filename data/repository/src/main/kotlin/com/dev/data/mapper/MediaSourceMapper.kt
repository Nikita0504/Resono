package com.dev.data.mapper

import com.dev.domain.model.BaseMediaData
import com.dev.domain.model.LocalMediaData
import com.dev.domain.model.MediaFile
import com.dev.domain.model.MediaSource
import com.dev.domain.model.RemoteMediaData

// Временные data classes для реализации интерфейсов
data class LocalMediaDataImpl(
    override val localUri: String,
    override val fileSize: Long,
    override val dateAdded: Long,
) : LocalMediaData

data class RemoteMediaDataImpl(
    override val remoteId: String,
    override val remoteUrl: String,
    override val isDownloaded: Boolean,
) : RemoteMediaData

/**
 * Маппер для создания MediaSource из опциональных данных
 */
object MediaSourceMapper {

    fun createMediaSource(
        localUri: String?,
        fileSize: Long?,
        dateAdded: Long?,
        remoteId: String?,
        remoteUrl: String?,
        isDownloaded: Boolean?,
    ): MediaSource? {
        val hasLocal = localUri != null
        val hasRemote = remoteId != null && remoteUrl != null

        return when {
            hasLocal && hasRemote -> {
                MediaSource.Synced(
                    local = LocalMediaDataImpl(localUri!!, fileSize ?: 0L, dateAdded ?: 0L),
                    remote = RemoteMediaDataImpl(remoteId!!, remoteUrl!!, isDownloaded ?: false),
                )
            }
            hasLocal -> {
                MediaSource.LocalOnly(
                    local = LocalMediaDataImpl(localUri!!, fileSize ?: 0L, dateAdded ?: 0L),
                )
            }
            hasRemote -> {
                MediaSource.RemoteOnly(
                    remote = RemoteMediaDataImpl(remoteId!!, remoteUrl!!, isDownloaded ?: false),
                )
            }
            else -> null
        }
    }
}
